(ns clj-dgraph.core
  (:import [clojure.lang IPersistentVector PersistentHashMap Keyword ISeq Named]))

(declare render-struct)

(defn vec->query [vecc]
  (render-struct vecc))

(defn- render-nquad [nquad]
  (clojure.string/join nquad))

(defn- render-func [func-def content]
  (let [{:keys [func args]} func-def]
    (str
     (render-struct func)
     "("  (cond
            (map? args) (clojure.string/join ","
                         (map (fn [[k v]]
                                (str (render-struct k)
                                     (when v (str ":" (render-struct v))))) args))
            (vector? args) (clojure.string/join ", " (mapv render-struct args))) ")"
     " {" (render-struct content) "}")))

(defn- render-quoted [el]
  (str "\"" el "\""))

(defn- render-nquad [el]
  el)

(defn- render-nested
  [el content]
  {:pre [(vector? content)]}
  (str (name el ) "{\n" (render-struct content) "\n}"))

(defn- render-vector [c]
  (let [fst (first c)
        scn (second c)]
    (cond
      (map? fst) (render-func fst (into [] (rest c)))
      (some? (#{:query :set :delete :mutation} fst)) (render-nested fst scn)
      :else (str (clojure.string/join "\n" (mapv render-struct c)) "\n"))))

(defn- render-string  
  [s]
  (if (= (first s) \<)
    (render-nquad s)
    (render-quoted s)))

;; protocol

(defprotocol GraphQLplusminus
  (render-struct [this]))

(extend-protocol GraphQLplusminus
  IPersistentVector
  (render-struct [this]
    (render-vector this))
  
  String
  (render-struct [this]
    (render-string this))
  
  Long
  (render-struct [this]
    (str this))
  
  Keyword
  (render-struct [this]
    (name this))
  
  nil
  (render-struct [this]
    "")
  )
