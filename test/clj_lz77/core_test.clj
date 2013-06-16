(ns clj-lz77.core-test
  (:use clojure.test
        clj-lz77.core
        clj-lz77.utils
        clj-lz77.encoder
        clj-lz77.decoder))

(deftest test-encode-decode
  (testing
    "Deconding an encoded sequence gives the original sequence"
    (are [x] (= x (decode (encode x)))
      [44 78 93 100 32 12 56 56 56 34 46 37]
      [23 34 56 78 23 34 56 78]
      [35 46 78 68 39 20 1 2 3 4 35 46 78 68 39 20 5 6 7 8])))