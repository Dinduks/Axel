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

(defn save-to-disk
  [file-channel-fut]
  (fn
    [promises-hash]
    (let [content-in-bytes (. (:body @(:promise promises-hash)) getBytes)
          ƒ (fn [fc] ((. fc write (ByteBuffer/wrap content-in-bytes)
                                  (:start-offset promises-hash))))]
      (println "Finished downloading part" (inc (:index promises-hash)))
      (map ƒ file-channel-fut)
      (println "Finished writing part    " (inc (:index promises-hash)) "to disk."))))

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
                  file-channel-fut (get-allocated-file-channel dest cont-len)]
              (pmap (save-to-disk file-channel-fut) promises)))))
