;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.decoder
  (:use clj-lz77.utils
        clj-lz77.constants))

(defn- copy-by-reference
  [dict distance length]
  (loop [a [] d dict l length]
    (if (pos? l)
      (let [pos (- (count d) distance)
            s (d pos)]
        (recur (conj a s) (conj d s) (dec l)))
      [a d])
    ))

(defn- decode*
  [xs dict]
  (when (seq xs)
    (let [c (first xs)
          dict (trim-vec dict default-search-buffer)]
      (cond
        (and
          (>= c 1)
          (<= c 8))
        (let [bytes (take c (drop 1 xs))]
          (lazy-cat bytes (decode* (drop (inc c) xs) (into dict bytes))))

        (<= c 0x7F)
        (lazy-cat [c] (decode* (rest xs) (conj dict c)))

        (>= c 0xC0)
        (let [c (bit-xor c 0x80)]
          (lazy-cat [32 c] (decode* (rest xs) (conj dict 32 c))))

        :else
        (let [[distance length] (decode-pair c (second xs))]
          (let [[bytes dict] (copy-by-reference dict distance length)]
            (lazy-cat bytes (decode* (drop 2 xs) dict))))
        ))))

(defn decode
  "Produces a sequence decompressed with LZ77 algorithm."
  [xs]
  (map ubyte->byte (decode* (map byte->ubyte xs) [])))
