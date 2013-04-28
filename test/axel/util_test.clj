(ns axel.util-test
  (:require [clojure.test :refer :all]
            [axel.util :refer :all]))

(deftest get-parts-size-test
  (testing
    (is (= (get-parts-size 8 4) [2 2 2 2]))
    (is (= (get-parts-size 9 4) [2 2 2 3]))))

(deftest get-start-offset-test
  (testing
    (is (= (get-start-offset 4 0) 0))
    (is (= (get-start-offset 4 1) 4))
    (is (= (get-start-offset 4 2) 8))
    (is (= (get-start-offset 4 3) 12))))

(deftest get-end-offset-test
  (testing
    (is (= (get-end-offset 21 85 0 4) 21))
    (is (= (get-end-offset 21 85 1 4) 42))
    (is (= (get-end-offset 21 85 2 4) 63))
    (is (= (get-end-offset 22 85 3 4) 85))))
