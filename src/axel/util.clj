(ns axel.util)

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
  [len n]
  (* n len))

(defn get-end-offset
  [len lentotal n ntotal]
  (cond
    (= (+ n 1) ntotal) lentotal
    :else (* (+ n 1) len)))
