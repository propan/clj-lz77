(defproject clj-lz77 "0.2.0-SNAPSHOT"
  :description "an implementation of LZ77 compression algorithm written in Clojure"
  :url "https://github.com/propan/clj-lz77"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[lein-kibit "0.0.8"]]
  :jvm-opts ^:replace []
  :global-vars {*warn-on-reflection* true})
