(ns clj-lz77.encoder-test
  (:use clojure.test
        clj-lz77.encoder))

(deftest test-encode
  (testing
    "Correctly encodes sequence of bytes"
    (are [xs exp] (= exp (encode xs))
      [1 2 3 4 5 6 7 8 9 10] [7 1 2 3 4 5 6 7 1 8 9 10]
      [88 89 95 101] [88 89 95 101]
      [34 56 83 32 78 95] [34 56 83 206 95]
      [88 89 95 101 89 95 101 83 15] [88 89 95 101 -128 24 83 15])))

