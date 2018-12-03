<span class="project-title"><span class="project-name">Planisphere</span>
===========================================================================================================================

![alt text](https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Planisphere.jpg/238px-Planisphere.jpg "PLANISPHERE")

Generate, view, and export vega.js and vega-lite.js plots from the command line or a Clojure REPL.



Installation
------------

To use the CLI executable, clone this repo and run the contents of the cli-executable folder.

To use as a Clojure library, add the following dependency to your project or build file:

``` deps
[planisphere "0.1.9"]
```

About
------

Planisphere is a fork/continuation of <https://github.com/yieldbot/vizard>. It keeps vizard’s nice vega spec generation facilities and offers a complete re-write of the gutty-works, adding these notable features:

-   The ability to use a headless, pure Java browser to render plot specifications via JavaScript. (no PhantomJS required!)

-   The ability to fetch the SVG string of a rendered plot and export it to a dang-ol jay-peg.

-   A command line interface to render JSON files containing Vega or Vega-Lite specifications.

-   The ability to view multiple plots when using a desktop browser.

-   Vega and Vega-Lite specification validation.

-   Other operational improvements around: client-server error handling, serialization.

<a href="#prerequisites" id="prerequisites"></a>Prerequisites
-------------------------------------------------------------

-   Oracle Java 8 with JavaFX

Usage
------
1.  Clojure API: see the planisphere.api.core docs below

2.  Command line: see the help summary (below)

Configuration Options & CLI
------

### <a href="#default-configuration-options" id="default-configuration-options"></a>Default Configuration Options

    ;; default configuration map

    {:log-level :info
     :host "localhost"
     :port 10666
     :headless? false
     :vega-schema "vega-schema.json"
     :vega-lite-schema "vega-lite-schema.json"
     :ascii "ascii.txt"}

### <a href="#overriding-default-configuration-options" id="overriding-default-configuration-options"></a>Overriding Default Configuration Options

From clojure, you can provide custom configuration options by passing a map into `planisphere.api.core/start-app`, e.g.:

    (start-app {:port 8080 :headless? true})

From the cli, configuration parameters can be passed in as options:

    ./planisphere -h
    16-11-30 21:28:04 rpurrier-mbp INFO [planisphere.cli.core:98] - Configuration options: {:host "localhost", :port 10666, :help true}
    16-11-30 21:28:04 rpurrier-mbp DEBUG [planisphere.cli.core:71] - Exiting: 0
     Usage: planisphere [options] spec-type
    Spec Types:
     vega
     vega-lite

    Options:
      -H, --host HOST          localhost  Server host
      -p, --port PORT          10666      Port number (0 = first available port)
          --headless                      Use the headless browser client
      -l, --log-level LEVEL               Server & client logging level
      -o, --output-dir DIR                Output directory for rendered plots
      -d, --input-dir SPECDIR             Directory containing vega or vega-lite specs
      -f, --input-file SPEC               File containing a vega or vega-lite spec
      -h, --help

### <a href="#important-knobs" id="important-knobs"></a>Important knobs

`:headless?` toggles the browser used for plot rendering: `true` = opens the headless jBrowserDriver client. `false` = opens your default desktop browser client.

`:log-level` controls console logging output for both server and client. For example, set to `:debug` when you need to dig into a potential JavaScript error in your browser console.

### <a href="#other-knobs" id="other-knobs"></a>Other knobs

`:vega-schema` and `:vega-lite-schema` locations of the JSON schemas that are used for validation of Vega and Vega-Lite specs.

`:ascii` change the ascii art on the landing page.

Clojure API: planisphere.api.core
------

### send-spec

`(send-spec m & {:keys [return], :or {return [:id :svg]}})`

Send a :spec of :type :vega or :vega-lite to the client browser. Given map should also contain a user-specified :id . :spec will be validated against the corresponding Vega/Vega-Lite schema before being sent for rendering.

### start-app

`(start-app user-opts)`

Start the application server and connect a client browser. Client can be configured as headless for server-side execution.

### stop-app

`(stop-app)`

Terminate the client session and the application server.

### svg-&gt;jpeg

`(svg->jpeg filename svg)`

Render an svg string to a jpeg file.

### validate-spec

`(validate-spec m & {:as schemas})`

Validate a Vega or Vega-Lite plot :spec. :spec can be given as a Clojure map or JSON string. This function can be used outside of the start-app workflow.
