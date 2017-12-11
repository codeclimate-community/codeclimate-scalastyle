# Code Climate scalastyle Engine

`codeclimate-scalastyle` is a Code Climate engine that wraps [scalastyle](http://www.scalastyle.org/). You can run it on your command line using the Code Climate CLI, or on our hosted analysis platform.

`scalastyle` is a configurable style linter for Scala code.

### Building
1. Install [sbt](http://www.scala-sbt.org/)
2. Run `sbt docker`


### Building release docker image
1. Run `sbt assembly && cp target/scala-2.12/codeclimate-scalastyle-assembly-<version>.jar ./`.
   This will create assembled jar with all dependencies.
2. Run `docker build -t codeclimate/codeclimate-scalastyle .`    

### Installation

1. If you haven't already, [install the Code Climate CLI](https://github.com/codeclimate/codeclimate).
2. Run `codeclimate engines:enable scalastyle`. This command both installs the engine and enables it in your `.codeclimate.yml` file.
3. You're ready to analyze! Browse into your project's folder and run `codeclimate analyze`.

### Need help?

For help with `scalastyle`, [check out their documentation](http://www.scalastyle.org/).

If you're running into a Code Climate issue, first look over this project's [GitHub Issues](https://github.com/codeclimate-community/codeclimate-scalastyle/issues), as your question may have already been covered. If not, [go ahead and open a support ticket with us](https://codeclimate.com/help).
