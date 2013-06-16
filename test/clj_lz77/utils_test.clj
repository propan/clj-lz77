;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.utils-test
  (:use clojure.test
        clj-lz77.utils))

(declare encode-decode-pair)

(deftest test-push-to-vec
  (testing
    "Tests correct pushing from a sequence to a vector"
    (let [v [0 0 0 0]
          xs [1 2 3 4]]
      (are [n result] (= result (push-to-vec v xs n))
        1 [[0 0 0 1] 1]
        2 [[0 0 1 2] 2]
        3 [[0 1 2 3] 3]
        4 [[1 2 3 4] 4]
        5 [[2 3 4 0] 4]
        6 [[3 4 0 0] 4]))))

(deftest test-encode-decode-pair
  (testing "Decoding encoded pair"
    (are [distance length] (= [distance length] (encode-decode-pair distance length))
      10 4
      100 6
      512 7
      925 5
      1322 3
      2046 3)))

;
; Helper functions
;

(defn encode-decode-pair
  [distance length]
  (let [[b0 b1] (encode-pair distance length)
        b0 (byte->ubyte b0)
        b1 (byte->ubyte b1)]
    (decode-pair b0 b1)))