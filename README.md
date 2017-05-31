# clj-dgraph

Clojure library to turn Clojure vectors and maps into Dgraph query strings

[![Clojars Project](https://img.shields.io/clojars/v/clj-dgraph.svg)](https://clojars.org/clj-dgraph)

## Usage


```
(require 'clj-dgraph.core)

(def query
  [:query
   [:director.name
    [{:func :me, :args {:id 1}}
     [{:func :allofterms :args [:name "quoted-name"]} :title2 :title3 ]]]])

(clj-dgraph.core/vec->query query)

```
Will return (non-pretty printed):

```
query{
    director.name
    me(id:1) {
        allofterms(name, "quoted-name") {
            title2
            title3
        }
    }
}
```
# TODO

- [ ] Support schema definitions
- [ ] Add some syntactic sugar for queries

## License

Copyright © 2017 Aleksandr Guljajev

Distributed under the MIT License.
