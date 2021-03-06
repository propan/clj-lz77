;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lz77.core
  (:use clj-lz77.encoder
        clj-lz77.decoder)
  (:import [java.nio ByteBuffer]
           [java.nio.channels ByteChannel]
           [java.io FileInputStream FileOutputStream]))

(def ^:const buffer-size 1024)

(defn- byte-seq
  [^ByteChannel channel]
  (let [buffer (ByteBuffer/allocate buffer-size)
        read (.read channel buffer)]
    (when (pos? read)
      (lazy-cat
        (take read (seq (-> buffer .flip .array)))
        (byte-seq channel)))))

(defn- write-seq
  [^ByteChannel channel xs]
  (let [buf (ByteBuffer/allocate buffer-size)]
    (loop [xs xs]
      (let [data (vec (take (.capacity buf) xs))
            read (count data)]
        (when (pos? read)
          (doseq [x data]
            (.put buf (byte x)))
          (.flip buf)
          (.write channel buf)
          (.clear buf)
          (recur (drop read xs)))))))

(defn- transform-file
  [^String src ^String dst tran-fn]
  (with-open [in (.getChannel (FileInputStream. src))
              out (.getChannel (FileOutputStream. dst))]
    (let [input (byte-seq in)
          output (tran-fn input)]
      (write-seq out output))))

(defn compress-file
  "Compreses file src to dst."
  [src dst]
  (transform-file src dst encode))

(defn decompress-file
  "Decompreses file src to dst."
  [src dst]
  (transform-file src dst decode))