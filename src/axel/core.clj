(ns axel.core
  (:require [org.httpkit.client :as client]
            [axel.util :refer :all]))

(defn create-promises
  [url cont-len]
  (fn
    [i x]
    (let [start-offset (get-start-offset cont-len i 4)
          end-offset   (- (get-end-offset x cont-len i 4) 1)
          options      {:headers { "Range" (format "bytes=%d-%d"
                                                   start-offset
                                                   end-offset)}}]
      {:promise (client/get url options)
       :start-offset start-offset
       :end-offset end-offset})))

(defn save-to-disk
  [i promises-hash]
  :body @(get promises-hash :promise))

(defn -main
  [& args]
  (let [dest "/tmp/samy"
        url "http://samy.dindane.com/samy"
        headers (:headers @(client/get url))
        cont-len (int (read-string (get headers :content-length)))
        parts-size (get-parts-size cont-len 4)
        promises (map-indexed (create-promises url cont-len) parts-size)]
    (alloc-disk-space dest cont-len)
    (map-indexed save-to-disk promises)))
