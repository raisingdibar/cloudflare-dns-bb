#!/usr/bin/env bb

(require '[babashka.http-client :as http])
(require '[clojure.java.io :as io]) ;; optional
(require '[clojure.edn :as edn])
(require '[cheshire.core :as json]) ;; optional
(require '[clojure.pprint :as pprint])

(def env
  (-> (slurp ".env.edn")
      edn/read-string))

(pprint/pprint env)

(defn get-records
  []
  (http/request {:uri {:scheme "https"
                       :host   "api.cloudflare.com"
                       :port   443
                       :path   (str "/client/v4/zones/" (:zone env) "/dns_records")}
                 :headers {:authorization (str "Bearer " (:token env))}
                 :method :get}))

(defn delete-record
  [id]
  (http/request {:uri {:scheme "https"
                       :host   "api.cloudflare.com"
                       :port   443
                       :path   (str "/client/v4/zones/" (:zone env) "/dns_records/" id)}
                 :headers {:authorization (str "Bearer " (:token env))}
                 :method :delete}))



(defn delete-records
  [records]
  (map #(delete-record (:id %)) records))

;; (defn record-type-map
;;   [records]
;;   (let [zone-records-for-type (fn [t]
;;                                   (filter #(= t (:type %)) records))]
;;     {:a (zone-records-for-type "A")
;;      :txt (zone-records-for-type "TXT")
;;      :cname (zone-records-for-type "CNAME")
;;      :srv (zone-records-for-type "SRV")
;;      :mx (zone-records-for-type "MX")}))

(def zone-records (-> (get-records)
                      :body
                      (json/parse-string true)
                      :result))

(pprint/pprint zone-records)

(comment 

  (def records-by-type (group-by :type zone-records))

  (delete-records (records-by-type "SRV"))
;; ;;   (delete-records (:a (record-type-map zone-records)))
;;   (delete-records (filter
;;                    #(not= (:name %) "default._domainkey.rootsandladders.com")
;;                    (:txt (record-type-map zone-records))))
  (delete-records (records-by-type "MX"))
  
  )