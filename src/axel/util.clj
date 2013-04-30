(ns axel.util
  (:import (java.nio.channels FileChannel)
           (java.nio.file Paths StandardOpenOption)
           (java.nio ByteBuffer)))

(defn get-parts-size-step
  [rest_ part-size iteration acc]
  (cond
    (= iteration 0) (conj acc rest_)
    :else (get-parts-size-step
            (- rest_ part-size)
            part-size
            (- iteration 1)
            (conj acc part-size))))

(defn get-parts-size
  [size nparts]
  (get-parts-size-step
    size
    (int (Math/floor (/ size nparts)))
    (- nparts 1)
    []))

(defn get-start-offset
  [lentotal n ntotal]
  (* n (int (Math/floor (/ lentotal ntotal)))))

(defn get-end-offset
  [len lentotal n ntotal]
  (cond
    (= (+ n 1) ntotal) lentotal
    :else (* (+ n 1) len)))

(defn get-allocated-file-channel
  [dest size]
  (let [file-channel (FileChannel/open (Paths/get dest (into-array [""]))
                                       (into-array [StandardOpenOption/CREATE
                                                    StandardOpenOption/WRITE
                                                    StandardOpenOption/SPARSE]))]
    (. file-channel write (ByteBuffer/allocate size))
    file-channel))
