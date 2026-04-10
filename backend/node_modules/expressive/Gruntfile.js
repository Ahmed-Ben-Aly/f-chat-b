module.exports = (grunt) => {
  grunt.loadNpmTasks('grunt-eslint')
  grunt.loadNpmTasks('grunt-mocha-test')
  grunt.loadNpmTasks('grunt-travis-matrix')
  grunt.loadNpmTasks('grunt-shell')
  grunt.loadNpmTasks('grunt-simple-istanbul')
  grunt.loadNpmTasks('grunt-open')

  grunt.initConfig({
    eslint: {
      lib: {
        options: {
          configFile: '.eslint.json',
          format: 'node_modules/eslint-codeframe-formatter'
        },
        src: ['lib/**/*.js']
      }
    },
    istanbul: {
      cover: {
        options: {
          hookRunInContext: true,
          root: 'lib',
          dir: 'coverage',
          x: ['**/node_modules/**'],
          simple: {
            cmd: 'cover',
            args: ['grunt', 'mochaTest:test'],
            rawArgs: ['--', '--color']
          }
        }
      }
    },
    mochaTest: {
      options: {
        reporter: 'spec',
        ui: 'mocha-given',
        require: 'coffee-script/register'
      },
      test: {
        src: ['test/**/*.coffee']
      }
    },
    travisMatrix: {
      v4: {
        test: () => {
          return /^v4/.test(process.version)
        },
        tasks: ['istanbul:cover', 'shell:codeclimate']
      }
    },
    shell: {
      codeclimate: 'npm run codeclimate'
    },
    open: {
      coverage: {
        path: 'coverage/lcov-report/index.html'
      }
    }
  });

  grunt.registerTask('mocha', ['mochaTest:test'])
  grunt.registerTask('default', ['eslint:lib', 'mocha'])
  grunt.registerTask('coverage', ['istanbul:cover'])
  grunt.registerTask('ci', ['eslint:lib', 'mocha', 'travisMatrix:v4'])
}
