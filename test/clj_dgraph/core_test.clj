(ns clj-dgraph.core-test
  (:require [clojure.test :refer :all]
            [clj-dgraph.core :refer :all]))

(def query
  [:query
   [:director.name
    [{:func :me, :args {:id 1}}
     [{:func :allofterms :args [:name "quoted-name"]} :title2 :title3 ]]
    ]
   ]
  )

(def mutation
  [:mutation
   [:set
    ["<id> <test> 1 ."]]])

(def deletion
  [:mutation
   [:delete
    ["<id> <test> 1 ."]]]
  )

(deftest test-query-or-set-non-vec
  (testing "Non-vec after in nested render"
    (is (thrown? AssertionError (vec->query [:query :this :will :fail])))))

(deftest test-render-func
  (testing "Testing query components are present"
    (let [parsed (vec->query query)]
      (is (.startsWith parsed "query{"))
      (is (.contains  parsed "me(id:1)"))
      (is (.contains  parsed "allofterms(name, \"quoted-name\")")))))

(deftest test-render-literals
  (testing "Quoted string"
    (is (= "\"name\"") (render-struct "name")))
  (testing "Key render"
    (is (= "name") (render-struct :name)))
  (testing "Literals vec"
    (is (= "name\nother\n" (render-struct [:name :other])))))

(deftest test-nquads
  (testing "Render nquads"
    (let [els ["<id> <test> 1 ." "<id> <test> 2 ."]
          rnd (render-struct els)]
      (is (= (str (clojure.string/join "\n" els) "\n"))))))
