(ns axel.util)

(defn get-parts-size-step
  [rest_ total-size nparts iteration acc]
  (cond
    (= iteration 0) (conj acc rest_)
    :else (let [part-size (int (Math/floor (/ total-size nparts)))]
           (get-parts-size-step
             (- rest_ part-size)
             total-size
             nparts
             (- iteration 1)
             (conj acc part-size)))))

(defn get-parts-size
  [size nparts]
  (get-parts-size-step size size nparts (- nparts 1) (vector)))
