(defproject planisphere "0.1.9"
  :description "Generate, view, and export vega.js and vega-lite.js plots from the command line or a Clojure REPL."
  :dependencies [[cheshire "5.5.0"]
                 [clj-http "2.2.0"]
                 [cljsjs/highlight "9.6.0-0"]
                 [cljsjs/react-dom "15.2.1-1"]
                 [cljsjs/react-dom-server "15.2.1-1"]
                 [cljsjs/vega "2.6.0-0"]
                 [cljsjs/vega-lite "1.2.0-0"]
                 [com.cognitect/transit-clj  "0.8.285"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.rpl/specter "0.9.1"]
                 [com.taoensso/encore "2.79.1"]
                 [com.taoensso/sente "1.9.0"]
                 [com.taoensso/timbre "4.5.1"]
                 [compojure "1.4.0"]
                 [cprop "0.1.9"]
                 [day8.re-frame/async-flow-fx "0.0.6"]
                 [diehard "0.4.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.2.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [metosin/scjsv "0.4.0"]
                 [mount "0.1.10"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/tools.cli "0.3.5"]
                 [re-frame "0.8.0"]
                 [reagent "0.6.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [sablono "0.3.6"]
                 [sparkledriver "0.1.1"]
                 [tikkba "0.6.0"]]
  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.2"]
	    [lein-codox "0.10.3"]]
  :source-paths ["src/clj"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "planisphere.core/on-js-reload"}
                        :compiler {:main planisphere.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/planisphere.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/planisphere.js"
                                   :main planisphere.core
                                   :optimizations :advanced
                                   :pretty-print false}
                        :jar true}]}
  :figwheel {:css-dirs ["resources/public/css"]}
  :codox {:namespaces [planisphere.api.core]
          :metadata {:doc/format :markdown}}
  :bin {:name "planisphere"}
  :profiles {:dev {:main planisphere.api.core
                   :source-paths ["src/cljs"]
                   :dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.2"]]}
             :cli {:omit-source true
                   :main planisphere.cli.core
                   :aot :all}
             :lib {:omit-source true
                   :aot :all
                   :uberjar-name "planisphere.jar"}})
