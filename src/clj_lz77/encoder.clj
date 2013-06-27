;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.encoder
  (:use clj-lz77.utils
        clj-lz77.constants))

(defn- match?
  [source search ^long pos]
  (let [search-length (count search)]
    (loop [i 0]
      (cond
        (and
          (< i search-length)
          (let [f (source (unchecked-add i pos)) s (search i)]
            (== f s)))
        (recur (unchecked-inc i))

        (>= i search-length)
        true

        :else false))))

(defn- find-match
  [src ^long start ^long limit ^long min-match]
  (let [search-bound (- (count src) limit)]
    (loop [position 0 match-start 0 match-length 0 search (subvec src start (+ start min-match))]
      (let [search-length (count search)]
        (if (>= position start)
          (if (zero? match-length)
            [0 0 (get src (+ start match-length))]
            [(- start match-start) match-length (get src (+ start match-length))])
          (if (match? src search position)
            (let [next-search-bound (+ start search-length 1)]
              (if (<= next-search-bound search-bound)
                (recur position position search-length (subvec src start next-search-bound))
                (recur start position search-length search)))
            (recur (inc position) match-start match-length search)))))))

(defn- literal?
  [^long x]
  (or
    (zero? x)
    (and (> x 8)
      (< x 0x80))))

(defn- produce-output
  [[distance length nval] position buffer look-ahead]
  (let [nnpos (+ position length 1)
        nnval (when (< nnpos (count buffer))
                (buffer nnpos))]
    (cond
      (>= length min-bytes-to-decode)
      [(encode-pair distance length) length]

      (and
        (= 32 nval)
        (not (nil? nnval))
        (and
          (>= nnval 0x40)
          (<= nnval 0x7F)))
      [[(bit-xor nnval 0x80)] 2]

      (literal? nval)
      [[nval] 1]

      :else (let [bytes (drop position buffer)
                  bytes (take look-ahead (take-while (complement literal?) bytes))
                  size (count bytes)]
              [(cons size bytes) size])
      )))

(defn- encode*
  [xs buf look-ahead]
  (if (pos? look-ahead)
    (let [limit (- default-look-ahead look-ahead)
          [distance length nval] (find-match buf default-search-buffer limit min-bytes-to-decode)
          [ys offset] (produce-output [distance length nval] default-search-buffer buf look-ahead)
          [buf read] (push-to-vec buf xs offset)
          look-ahead (+ (- look-ahead offset) read)]
      (lazy-cat ys (encode* (drop read xs) buf look-ahead)))))


(defn encode
  "Produces a sequence compressed with LZ77 algorithm."
  [xs]
  (let [xs (map byte->ubyte xs)
        look-ahead default-look-ahead
        buf-size (+ default-search-buffer look-ahead)
        buf (vec (repeat buf-size 0))
        [buf read] (push-to-vec buf xs look-ahead)
        look-ahead (min read look-ahead)]
    (map ubyte->byte (encode* (drop read xs) buf look-ahead))))