(ns clj-lz77.utils)

(defn push-to-vec
  "Pushes to a vector v n elements from a sequence xs, preserving the size of v.
   Returns a tuple [v r] of a new vector and an amount of read items."
  [v xs n]
  (let [data (take n xs)
        read (count data)
        data (if (= n read)
               data
               (concat data (repeat (- n read) 0)))]
    [(subvec (into v data) n) read]))

(defn trim-vec
  "Returns a trimmed vector if it's longer then the given size."
  [v size]
  (let [overflow (- (count v) size)]
    (if (pos? overflow)
      (subvec v overflow)
      v)))

(defn ubyte->byte
  "Returns the value of the given unsigned byte as an byte."
  [^long x]
  (.byteValue (Integer/valueOf x)))

(defn byte->ubyte
  "Returns the value of the given byte as an integer, when treated as unsigned."
  [^long x]
  (bit-and 0xFF (int x)))

(defn encode-pair
  "Encodes distance and length into a two bytes pair."
  [^long distance ^long length]
  (let [compound (+ (bit-shift-left distance 3) length -3)
        b0 (+ 0x80 (bit-shift-right compound 8))
        b1 (bit-and compound 0xFF)]
    [b0 b1]))

(defn decode-pair
  "Decodes two bytes into a destance-length pair."
  [b0 b1]
  (let [compound (+ (bit-shift-left b0 8) b1)
        distance (bit-shift-right (bit-and compound 0x3FFF) 3)
        length (+ (bit-and compound 7) 3)]
    [distance length]))