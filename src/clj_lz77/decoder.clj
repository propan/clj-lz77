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
  (when-not (empty? xs)
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

        :else (let [[distance length] (decode-pair c (second xs))
                    [bytes dict] (copy-by-reference dict distance length)]
                (lazy-cat bytes (decode* (drop 2 xs) dict)))
        ))))

(defn decode
  "Produces a sequence decompressed with LZ77 algorithm."
  [xs]
  (decode* (map byte->ubyte xs) []))
