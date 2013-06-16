;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.encoder-test
  (:use clojure.test
        clj-lz77.encoder))

(deftest test-encode
  (testing
    "Correctly encodes sequence of bytes"
    (are [xs exp] (= exp (encode xs))
      [1 2 3 4 5 6 7 8 9 10] [7 1 2 3 4 5 6 7 1 8 9 10]
      [88 89 95 101] [88 89 95 101]
      [-30 -128 -103] [3 -30 -128 -103]
      [34 56 83 32 78 95] [34 56 83 -50 95]
      [88 89 95 101 89 95 101 83 15] [88 89 95 101 -128 24 83 15])))

