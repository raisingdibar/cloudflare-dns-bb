#!/usr/bin/env bb

(ns list-domains
  (:require
   [babashka.http-client :as http]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [cheshire.core :as json]
   [clojure.pprint :as pprint]
   [clojure.string]))


(def env
  (-> (slurp ".env.edn")
      edn/read-string))

(pprint/pprint env)

(defn get-domain
  [domain-name]
  (http/request {:uri {:scheme "https"
                       :host   "api.cloudflare.com"
                       :port   443
                       :path   (str "/client/v4/accounts/" (:account-id env) "/registrar/domains/" domain-name)}
                 :headers {:X-Auth-Key (str (:global-token env))
                           :X-Auth-Email (str (:auth-email env))}
                 :method :get}))

(defn read-json-file [file-path]
  (with-open [reader (io/reader file-path)]
    (json/parse-stream reader true)))

(defn strip-scheme [url]
  (let [uri (java.net.URI. url)]
    (.getHost uri)))

(defn process-url [url]
  ;; Replace this with the method you want to call on each URL
  (let [stripped-url (strip-scheme url)]
    (-> (get-domain stripped-url)
        :body
        (json/parse-string true)
        :result)))

(comment
  (get-domain "tylerdibartolo.com")

  ;; (strip-scheme "yeet.com")
  (strip-scheme "https://yeet.com")

  (process-url "http://tylerdibartolo.com")
  (process-url "http://anotheruri.com")

  ;; Below doesn't work, needs scheme.
  ;; (process-url "yeet.com")

  (let [file-path "/Users/jtd/Developer/maritimejobs/urllist.json"
        urls (read-json-file file-path)]
    (map process-url urls))
 )