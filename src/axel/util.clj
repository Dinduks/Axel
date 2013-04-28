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
