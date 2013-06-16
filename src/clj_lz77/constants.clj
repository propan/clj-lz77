(ns clj-lz77.constants)

(def ^:const min-bytes-to-decode 3)

; There are 11-bits available (2^11 -1) in the encoding scheme that is used.
(def ^:const default-search-buffer 2047)
; There are 4-bits available (2^3 -1) in the encoding scheme that is used.
(def ^:const default-look-ahead 7)
