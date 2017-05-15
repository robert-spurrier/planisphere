(ns dev)

;; Declaring data vars up here just to keep their bulky forms out of the way
;; of the test specifications

(declare ^:dynamic vega-data)
(declare ^:dynamic vega-lite-data)

;; Test specifications

(def ^:dynamic vega
  "An example vega payload. :spec is a Clojure map, but can be a JSON string."
  {:return [:id :svg]
   :type :vega
   :id :test-spec
   :spec {:legends [{:fill "color"}]
          :axes
          [{:scale "x", :type :x, :title :xaxis} {:scale "y", :type :y, :title :yaxis}]
          :width 960
          :scales
          [{:name :x, :type :ordinal, :domain {:field :x, :data :bar}, :range :width}
           {:name :y
            :type :linear
            :nice true
            :domain {:field "sum_y", :data "stats"}
            :range :height}
           {:name :color
            :type "ordinal"
            :domain {:field :col, :data :bar}
            :range "category20b"}]
          :padding "auto"
          :marks
          [{:properties
            {:enter
             {:y {:scale "y", :field "layout_start"}
              :fill {:scale "color", :field :col}
              :Width {:offset -1, :scale "x", :band true}
              :x {:scale "x", :field :x}
              :y2 {:scale "y", :field "layout_end"}}}
            :type :rect
            :from
            {:transform [{:field :y, :type :stack, :sortby [:col], :groupby [:x]}]
             :data :bar}}]
          :height 500
          :data vega-data}})

(def ^:dynamic vega-lite
  "An example vega-lite payload. :spec is a Clojure map, but can be a JSON string."
  {:return [:id :svg]
   :type :vega-lite
   :id :lite-spec
   :spec {:encoding
          {:y {:field "y", :type "quantitative", :aggregate "sum"}
           :color {:scale {:range "category20b"}, :field "col", :type "nominal"}
           :x {:field "x", :type "ordinal"}}
          :mark "bar"
          :width 960
          :height 500
          :data vega-lite-data}})

;; Binding data vars down here vvv






;; Down some more even vv






;; Just a bit further v





;; Test specification data!

(def vega-data
  [{:name :bar
    :values
    [{:y 12, :col "foo", :x 0}  {:y 41, :col "bar", :x 0} {:y 58, :col "baz", :x 0} {:y 27, :col "poot", :x 0}
     {:y 99, :col "foo", :x 1}  {:y 48, :col "bar", :x 1} {:y 27, :col "baz", :x 1} {:y 80, :col "poot", :x 1}
     {:y 64, :col "foo", :x 2}  {:y 87, :col "bar", :x 2} {:y 80, :col "baz", :x 2} {:y 26, :col "poot", :x 2}
     {:y 31, :col "foo", :x 3}  {:y 26, :col "bar", :x 3} {:y 9, :col "baz", :x 3}  {:y 89, :col "poot", :x 3}
     {:y 32, :col "foo", :x 4}  {:y 2, :col "bar", :x 4}  {:y 23, :col "baz", :x 4} {:y 7, :col "poot", :x 4}
     {:y 20, :col "foo", :x 5}  {:y 72, :col "bar", :x 5} {:y 52, :col "baz", :x 5} {:y 72, :col "poot", :x 5}
     {:y 5, :col "foo", :x 6}   {:y 50, :col "bar", :x 6} {:y 13, :col "baz", :x 6} {:y 61, :col "poot", :x 6}
     {:y 52, :col "foo", :x 7}  {:y 24, :col "bar", :x 7} {:y 16, :col "baz", :x 7} {:y 69, :col "poot", :x 7}
     {:y 7, :col "foo", :x 8}   {:y 17, :col "bar", :x 8} {:y 42, :col "baz", :x 8} {:y 71, :col "poot", :x 8}
     {:y 54, :col "foo", :x 9}  {:y 69, :col "bar", :x 9} {:y 41, :col "baz", :x 9} {:y 75, :col "poot", :x 9}
     {:y 10, :col "foo", :x 10} {:y 63, :col "bar", :x 10}{:y 13, :col "baz", :x 10}{:y 42, :col "poot", :x 10}
     {:y 92, :col "foo", :x 11} {:y 3, :col "bar", :x 11} {:y 89, :col "baz", :x 11}{:y 65, :col "poot", :x 11}
     {:y 48, :col "foo", :x 12} {:y 54, :col "bar", :x 12}{:y 64, :col "baz", :x 12}{:y 18, :col "poot", :x 12}
     {:y 40, :col "foo", :x 13} {:y 57, :col "bar", :x 13}{:y 66, :col "baz", :x 13}{:y 48, :col "poot", :x 13}
     {:y 32, :col "foo", :x 14} {:y 66, :col "bar", :x 14}{:y 41, :col "baz", :x 14}{:y 47, :col "poot", :x 14}
     {:y 20, :col "foo", :x 15} {:y 92, :col "bar", :x 15}{:y 5, :col "baz", :x 15} {:y 26, :col "poot", :x 15}
     {:y 49, :col "foo", :x 16} {:y 46, :col "bar", :x 16}{:y 59, :col "baz", :x 16}{:y 4, :col "poot", :x 16}
     {:y 76, :col "foo", :x 17} {:y 8, :col "bar", :x 17} {:y 13, :col "baz", :x 17}{:y 95, :col "poot", :x 17}
     {:y 69, :col "foo", :x 18} {:y 58, :col "bar", :x 18}{:y 9, :col "baz", :x 18} {:y 19, :col "poot", :x 18}
     {:y 33, :col "foo", :x 19} {:y 26, :col "bar", :x 19}{:y 1, :col "baz", :x 19} {:y 7, :col "poot", :x 19}]}
   {:transform
    [{:type :aggregate, :groupby [:x], :summarize [{:field :y, :ops ["sum"]}]}]
    :name :stats
    :source :bar}])

(def vega-lite-data
  {:values
   [{:y 3, :col "foo", :x 0}   {:y 97, :col "bar", :x 0}  {:y 24, :col "baz", :x 0}  {:y 38, :col "buh", :x 0}  {:y 81, :col "bunk", :x 0}  {:y 93, :col "dunk", :x 0}
    {:y 10, :col "foo", :x 1}  {:y 85, :col "bar", :x 1}  {:y 71, :col "baz", :x 1}  {:y 68, :col "buh", :x 1}  {:y 60, :col "bunk", :x 1}  {:y 89, :col "dunk", :x 1}
    {:y 29, :col "foo", :x 2}  {:y 14, :col "bar", :x 2}  {:y 83, :col "baz", :x 2}  {:y 70, :col "buh", :x 2}  {:y 38, :col "bunk", :x 2}  {:y 4, :col "dunk", :x 2}
    {:y 28, :col "foo", :x 3}  {:y 96, :col "bar", :x 3}  {:y 22, :col "baz", :x 3}  {:y 9, :col "buh", :x 3}   {:y 3, :col "bunk", :x 3}   {:y 11, :col "dunk", :x 3}
    {:y 87, :col "foo", :x 4}  {:y 88, :col "bar", :x 4}  {:y 64, :col "baz", :x 4}  {:y 48, :col "buh", :x 4}  {:y 37, :col "bunk", :x 4}  {:y 9, :col "dunk", :x 4}
    {:y 56, :col "foo", :x 5}  {:y 46, :col "bar", :x 5}  {:y 49, :col "baz", :x 5}  {:y 42, :col "buh", :x 5}  {:y 49, :col "bunk", :x 5}  {:y 70, :col "dunk", :x 5}
    {:y 48, :col "foo", :x 6}  {:y 65, :col "bar", :x 6}  {:y 18, :col "baz", :x 6}  {:y 50, :col "buh", :x 6}  {:y 55, :col "bunk", :x 6}  {:y 39, :col "dunk", :x 6}
    {:y 25, :col "foo", :x 7}  {:y 39, :col "bar", :x 7}  {:y 43, :col "baz", :x 7}  {:y 93, :col "buh", :x 7}  {:y 94, :col "bunk", :x 7}  {:y 13, :col "dunk", :x 7}
    {:y 15, :col "foo", :x 8}  {:y 75, :col "bar", :x 8}  {:y 11, :col "baz", :x 8}  {:y 83, :col "buh", :x 8}  {:y 89, :col "bunk", :x 8}  {:y 35, :col "dunk", :x 8}
    {:y 35, :col "foo", :x 9}  {:y 29, :col "bar", :x 9}  {:y 9, :col "baz", :x 9}   {:y 26, :col "buh", :x 9}  {:y 90, :col "bunk", :x 9}  {:y 90, :col "dunk", :x 9}
    {:y 13, :col "foo", :x 10} {:y 19, :col "bar", :x 10} {:y 14, :col "baz", :x 10} {:y 71, :col "buh", :x 10} {:y 10, :col "bunk", :x 10} {:y 18, :col "dunk", :x 10}
    {:y 62, :col "foo", :x 11} {:y 56, :col "bar", :x 11} {:y 33, :col "baz", :x 11} {:y 55, :col "buh", :x 11} {:y 71, :col "bunk", :x 11} {:y 0, :col "dunk", :x 11}
    {:y 87, :col "foo", :x 12} {:y 6, :col "bar", :x 12}  {:y 66, :col "baz", :x 12} {:y 68, :col "buh", :x 12} {:y 38, :col "bunk", :x 12} {:y 95, :col "dunk", :x 12}
    {:y 24, :col "foo", :x 13} {:y 91, :col "bar", :x 13} {:y 89, :col "baz", :x 13} {:y 96, :col "buh", :x 13} {:y 4, :col "bunk", :x 13}  {:y 85, :col "dunk", :x 13}
    {:y 18, :col "foo", :x 14} {:y 65, :col "bar", :x 14} {:y 43, :col "baz", :x 14} {:y 72, :col "buh", :x 14} {:y 22, :col "bunk", :x 14} {:y 32, :col "dunk", :x 14}
    {:y 32, :col "foo", :x 15} {:y 62, :col "bar", :x 15} {:y 64, :col "baz", :x 15} {:y 14, :col "buh", :x 15} {:y 61, :col "bunk", :x 15} {:y 97, :col "dunk", :x 15}
    {:y 46, :col "foo", :x 16} {:y 59, :col "bar", :x 16} {:y 7, :col "baz", :x 16}  {:y 78, :col "buh", :x 16} {:y 49, :col "bunk", :x 16} {:y 70, :col "dunk", :x 16}
    {:y 3, :col "foo", :x 17}  {:y 26, :col "bar", :x 17} {:y 86, :col "baz", :x 17} {:y 66, :col "buh", :x 17} {:y 8, :col "bunk", :x 17}  {:y 81, :col "dunk", :x 17}
    {:y 69, :col "foo", :x 18} {:y 7, :col "bar", :x 18}  {:y 63, :col "baz", :x 18} {:y 15, :col "buh", :x 18} {:y 19, :col "bunk", :x 18} {:y 96, :col "dunk", :x 18}
    {:y 94, :col "foo", :x 19} {:y 29, :col "bar", :x 19} {:y 9, :col "baz", :x 19}  {:y 93, :col "buh", :x 19} {:y 32, :col "bunk", :x 19} {:y 78, :col "dunk", :x 19}]})


;; Bonus test spec...this one won't pass validation because it has been purposefully tampred with.


(def incorrect-vega-spec
  {:type :vega
   :id "malformed"
   :spec
   "{\n  \"width\": 500,\n  \"height\": 250,\n  \"padding\": \"auto\",\n  \"data\": [\n    {\n      \"values\": [\n        {\"year\": 1875,\"population\": 1309},\n        {\"year\": 1890,\"population\": 1558},\n        {\"year\": 1910,\"population\": 4512},\n        {\"year\": 1925,\"population\": 8180},\n        {\"year\": 1933,\"population\": 15915},\n        {\"year\": 1939,\"population\": 24824},\n        {\"year\": 1946,\"population\": 28275},\n        {\"year\": 1950,\"population\": 29189},\n        {\"year\": 1964,\"population\": 29881},\n        {\"year\": 1971,\"population\": 26007},\n        {\"year\": 1981,\"population\": 24029},\n        {\"year\": 1985,\"population\": 23340},\n        {\"year\": 1989,\"population\": 22307},\n        {\"year\": 1990,\"population\": 22087},\n        {\"year\": 1991,\"population\": 22139},\n        {\"year\": 1992,\"population\": 22105},\n        {\"year\": 1993,\"population\": 22242},\n        {\"year\": 1994,\"population\": 22801},\n        {\"year\": 1995,\"population\": 24273},\n        {\"year\": 1996,\"population\": 25640},\n        {\"year\": 1997,\"population\": 27393},\n        {\"year\": 1998,\"population\": 29505},\n        {\"year\": 1999,\"population\": 32124},\n        {\"year\": 2000,\"population\": 33791},\n        {\"year\": 2001,\"population\": 35297},\n        {\"year\": 2002,\"population\": 36179},\n        {\"year\": 2003,\"population\": 36829},\n        {\"year\": 2004,\"population\": 37493},\n        {\"year\": 2005,\"population\": 38376},\n        {\"year\": 2006,\"population\": 39008},\n        {\"year\": 2007,\"population\": 39366},\n        {\"year\": 2008,\"population\": 39821},\n        {\"year\": 2009,\"population\": 40179},\n        {\"year\": 2010,\"population\": 40511},\n        {\"year\": 2011,\"population\": 40465},\n        {\"year\": 2012,\"population\": 40905},\n        {\"year\": 2013,\"population\": 41258},\n        {\"year\": 2014,\"population\": 41777}\n      ]\n    },\n    {\n      \"name\": \"annotation\",\n      \"values\": [\n        {\"start\": 1933,\"end\": 1945,\"text\": \"Nazi rule\"},\n        {\n          \"start\": 1948,\n          \"end\": 1989,\n          \"text\": \"GDR (East Germany)\"\n        }\n      ]\n    }\n  ],\n  \"scales\": [\n    {\n      \"name\": \"dog\",\n      \"type\": \"funny\",\n      \"range\": \"width\",\n      \"zero\": false,\n      \"domain\": {\"data\": \"table\",\"field\": \"year\"}\n    },\n    {\n      \"name\": \"y\",\n      \"range\": \"height\",\n      \"nice\": true,\n      \"domain\": {\"data\": \"table\",\"field\": \"population\"}\n    },\n    {\n      \"name\": \"egg\",\n      \"type\": \"ordinal\",\n      \"domain\": {\"data\": \"annotation\",\"field\": \"text\"},\n      \"range\": [\"black\",\"red\"]\n    }\n  ],\n  \"axes\": [\n    {\n      \"type\": \"x\",\n      \"scale\": \"x\",\n      \"format\": \"d\",\n      \"title\": \"Year\",\n      \"ticks\": 15\n    },\n    {\n      \"type\": \"y\",\n      \"scale\": \"y\",\n      \"title\": \"Population\",\n      \"grid\": true,\n      \"layer\": \"back\"\n    }\n  ],\n  \"marks\": [\n    {\n      \"type\": \"rect\",\n      \"from\": {\"data\": \"annotation\"},\n      \"properties\": {\n        \"enter\": {\n          \"x\": {\"scale\": \"x\",\"field\": \"start\"},\n          \"x2\": {\"scale\": \"x\",\"field\": \"end\"},\n          \"y\": {\"value\": 0},\n          \"y2\": {\"signal\": \"height\"},\n          \"fill\": {\"scale\": \"color\",\"field\": \"text\"},\n          \"opacity\": {\"value\": 0.2}\n        }\n      }\n    },\n    {\n      \"type\": \"line\",\n      \"from\": {\"data\": \"table\"},\n      \"properties\": {\n        \"enter\": {\n          \"interpolate\": {\"value\": \"monotone\"},\n          \"x\": {\"scale\": \"x\",\"field\": \"year\"},\n          \"y\": {\"scale\": \"y\",\"field\": \"population\"},\n          \"stroke\": {\"value\": \"steelblue\"},\n          \"strokeWidth\": {\"value\": 3}\n        }\n      }\n    },\n    {\n      \"type\": \"symbol\",\n      \"from\": {\"data\": \"table\"},\n      \"properties\": {\n        \"enter\": {\n          \"x\": {\"scale\": \"x\",\"field\": \"year\"},\n          \"y\": {\"scale\": \"y\",\"field\": \"population\"},\n          \"stroke\": {\"value\": \"steelblue\"},\n          \"fill\": {\"value\": \"white\"},\n          \"size\": {\"value\": 30}\n        }\n      }\n    },\n    {\n      \"type\": \"text\",\n      \"from\": {\n        \"data\": \"table\",\n        \"transform\": [\n          {\n            \"type\": \"aggregate\",\n            \"summarize\": {\"year\": [\"min\",\"max\"]}\n          }\n        ]\n      },\n      \"properties\": {\n        \"enter\": {\n          \"x\": {\"signal\": \"width\",\"mult\": 0.5},\n          \"y\": {\"value\": -10},\n          \"text\": {\n            \"template\": \"Population of Falkensee from {{datum.min_year}} to {{datum.max_year}}\"\n          },\n          \"fill\": {\"value\": \"black\"},\n          \"fontSize\": {\"value\": 16},\n          \"align\": {\"value\": \"center\"},\n          \"fontWeight\": {\"value\": \"bold\"}\n        }\n      }\n    }\n  ],\n  \"legends\": [\n    {\n      \"fill\": \"color\",\n      \"title\": \"Period\",\n      \"properties\": {\n        \"symbols\": {\n          \"strokeWidth\": {\"value\": 0},\n          \"shape\": {\"value\": \"square\"},\n          \"opacity\": {\"value\": 0.3}\n        },\n        \"legend\": {\n          \"x\": {\"value\": 10},\n          \"y\": {\"value\": 5},\n          \"fill\": {\"value\": \"white\"}\n        }\n      }\n    }\n  ]\n}\n"})
