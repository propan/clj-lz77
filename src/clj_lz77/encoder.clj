(ns clj-lz77.encoder
  (:use clj-lz77.utils
        clj-lz77.constants))

(defn- find-match
  [s start limit]
  (loop [position 0 match-start 0 match-length 0 search (subvec s start (inc start))]
    (let [search-length (count search)
          next-bound (+ start search-length 1)]
      (if (or (>= position start)
            (>= next-bound (- (count s) limit)))
        (if (zero? match-length)
          [0 0 (s start)]
          [(- start match-start) match-length (s (+ start match-length))])
        (let [match (subvec s position (+ position search-length))]
          (if (= match search)
            (recur position position search-length (subvec s start next-bound))
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

      (> length 0)
      [(subvec buffer position (+ position length)) length]

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
    (let [[distance length nval] (find-match buf default-search-buffer (- default-look-ahead look-ahead))
          [ys offset] (produce-output [distance length nval] default-search-buffer buf look-ahead)
          [buf read] (push-to-vec buf xs offset)
          look-ahead (+ (- look-ahead offset) read)]
      (lazy-cat ys (encode* (drop read xs) buf look-ahead)))))


(defn encode
  "Produces a sequence compressed with LZ77 algorithm."
  [xs]
  (let [look-ahead default-look-ahead
        buf-size (+ default-search-buffer look-ahead)
        buf (vec (repeat buf-size 0))
        [buf read] (push-to-vec buf xs look-ahead)
        look-ahead (min read look-ahead)]
    (encode* (drop read xs) buf look-ahead)))