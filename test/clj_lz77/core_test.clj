;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.core-test
  (:use clojure.test
        clj-lz77.core
        clj-lz77.utils
        clj-lz77.encoder
        clj-lz77.decoder))


(defn to-string
  [xs]
  (String. (into-array Byte/TYPE xs)))

(deftest test-encode-decode
  (testing
    "Deconding an encoded sequence gives the original sequence"
    (are [x] (= x (decode (encode x)))
      [44 78 93 100 32 12 56 56 56 34 46 37]
      [23 34 56 78 23 34 56 78]
      [3 -30 -128 -103]
      [111 111 111 111 56 56 56 56 56 56 56]
      [35 46 78 68 39 20 1 2 3 4 35 46 78 68 39 20 5 6 7 8]))
  (testing
    "Correctly handls strings compression"
    (are [x] (= x (to-string (decode (encode (seq (.getBytes x))))))
      "This could be your advertisement!"
      "What's the color of the Moon, buddy?"
      "Bob’s cat was't here!"
      "The word “top” is my favorite.")))

(deftest test-comperss-decompress
  (testing
    "Compressing and decopressing produces the original file"
    (compress-file "/tmp/i.md" "/tmp/i.lz77")
    (decompress-file "/tmp/i.lz77" "/tmp/i-r.md")))