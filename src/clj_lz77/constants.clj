;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.constants)

(def ^:const min-bytes-to-decode 3)

; There are 11-bits available (2^11 -1) in the encoding scheme that is used.
(def ^:const default-search-buffer 2047)
; There are 4-bits available (2^3 -1) in the encoding scheme that is used.
(def ^:const default-look-ahead 7)
