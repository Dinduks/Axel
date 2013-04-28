(ns axel.util-test
  (:require [clojure.test :refer :all]
            [axel.util :refer :all]))

(deftest get-parts-size-test
  (testing
    (is (= (get-parts-size 8 4) [2 2 2 2]))
    (is (= (get-parts-size 9 4) [2 2 2 3]))))
