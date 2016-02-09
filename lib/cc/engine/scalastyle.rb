require 'nokogiri'
require 'json'

module CC
  module Engine
    class ScalaStyle
      def initialize(directory: , io: , engine_config: )
        @directory = directory
        @engine_config = engine_config
        @io = io
      end

      def run
        exec_scalastyle_with_options
        
        Dir.chdir(@directory) do
          next unless results
          results.xpath('//file').each do |file|
            path = file['name'].sub("/home/app/", "")
            file.children.each do |node|

	      lint = node.attributes

              issue = {
                type: "issue",
                check_name: lint["source"].value,
                description: lint["message"].value,
                categories: ["Style"],
                remediation_points: 50_000,
                location: {
                  path: path,
                  positions: {
                    begin: {
                      line: lint["line"].value.to_i,
                      column: lint["column"].value.to_i
                    },
                    end: {
                      line: lint["line"].value.to_i,
                      column: lint["column"].value.to_i
                    }
                  }
                }
              }

              puts("#{issue.to_json}\0")
            end
          end
        end
      end

      private

      def project_files
        Dir.glob("**/*scala")
      end

      def analysis_files
        exclusions = @engine_config['exclude_paths'] || []
        project_files.reject { |f| exclusions.include?(f) }.join(" ")
      end

      def scalastyle_jar
        '/home/app/scalastyle_2.11-0.6.0-batch.jar'
      end

      def scalastyle_config
        @engine_config['config'] || '/home/app/scalastyle_config.xml'
      end

      def results_xml_file_path
        "/home/app/results.xml"
      end

      def exec_scalastyle_with_options
        `java -jar #{scalastyle_jar} \
          -c #{scalastyle_config} \
          --xmlOutput #{results_xml_file_path} \
          #{analysis_files}`
      end

      def results
        if File.exists?(results_xml_file_path)
          @results ||= Nokogiri::XML(File.read(results_xml_file_path))
        end
      end
      
    end
  end
end
