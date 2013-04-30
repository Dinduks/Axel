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
  [file-channel]
  (fn
    [promises-hash]
    (let [content-in-bytes (. (:body @(:promise promises-hash)) getBytes)]
      (println "Finished downloading part" (inc (:index promises-hash)))
      (. file-channel write
         (ByteBuffer/wrap content-in-bytes)
         (:start-offset promises-hash))
      (println "Finished writing part    " (inc (:index promises-hash)) "to disk."))))

(defn -main
  [& args]
  (let [dest "/tmp/samy"
        url "http://samy.dindane.com/samy"
        headers (:headers @(client/head url {:headers {"Accept-Encoding" ""}}))
        cont-len (int (read-string (get headers :content-length)))
        parts-size (get-parts-size cont-len 4)
        promises (map-indexed (create-promises url cont-len dest) parts-size)
        file-channel (alloc-disk-space dest cont-len)] ; this shit's blocking
    (pmap (save-to-disk file-channel) promises)))
