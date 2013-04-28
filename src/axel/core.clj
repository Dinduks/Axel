(ns axel.core
  (:require [org.httpkit.client :as client]
            [axel.util :refer :all]))

(defn create-futures
  [url cont-len]
  (fn
    [i x]
    (let [options {:headers { "Range" (format
                                        "bytes=%d-%d"
                                        (get-start-offset cont-len i 4)
                                        (- (get-end-offset x cont-len i 4) 1))}}]
      (client/get url options))))

(defn save-to-disk
  [i fut]
  fut)

(defn -main
  [& args]
  (let [url "http://samy.dindane.com/samy"
        headers (:headers @(client/get url))
        cont-len (int (read-string (get headers :content-length)))
        parts-size (get-parts-size cont-len 4)
        futures (map-indexed (create-futures url cont-len) parts-size)]
    (map-indexed save-to-disk futures)))
