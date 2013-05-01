(ns axel.core
  (:require [org.httpkit.client :as client]
            [axel.util :refer :all])
  (:import (java.nio ByteBuffer)))

(defn create-promises
  [url cont-len dest]
  (fn
    [i x]
    (let [start-offset (get-start-offset cont-len i 4)
          end-offset   (- (get-end-offset x cont-len i 4) 1)
          options      {:headers {"Range" (format "bytes=%d-%d"
                                                  start-offset
                                                  end-offset)
                                  "Accept-Encoding" ""}}]
      {:promise (client/get url options)
       :start-offset start-offset
       :index i})))

(defn save-to-disk-ƒ
  [promises-hash content-in-bytes]
  (fn
    [fc-fut]
    (let [fc @fc-fut
          byte-buffer (ByteBuffer/wrap content-in-bytes)
          start-offset (:start-offset promises-hash)]
      (. fc write byte-buffer start-offset)
      ;; TODO: Make the message below appear
      (println "Finished writing part    " (inc (:index promises-hash)) "to disk.")
      ())))

(defn save-to-disk
  [file-channel-fut]
  (fn
    [promises-hash]
    (let [content-in-bytes (. (:body @(:promise promises-hash)) getBytes)
          ƒ (save-to-disk-ƒ promises-hash content-in-bytes)
          index (inc (:index promises-hash))]
      (println "Finished downloading part" index)
      (ƒ file-channel-fut)
      ()))) ; TODO: Remove the last useless s-exp

(defn -main
  [& args]
  (let [dest "/tmp/samy"
        url "http://samy.dindane.com/samy"
        response @(client/head url {:headers {"Accept-Encoding" ""}})]
    (cond
      (= (:status response) 404) (println "Error: File not found (404)")
      (= (:status response) 403) (println "Error: File forbidden (403)")
      :else (let [headers (:headers response)
                  cont-len (int (read-string (get headers :content-length)))
                  parts-size (get-parts-size cont-len 4)
                  promises (map-indexed (create-promises url cont-len dest) parts-size)
                  file-channel-fut (get-allocated-file-channel dest cont-len)
                  ƒ (save-to-disk file-channel-fut)]
              (pmap ƒ promises)))))
