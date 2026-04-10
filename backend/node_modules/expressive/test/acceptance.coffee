express = require 'express'
request = require 'supertest'

describe 'acceptance', ->
  Given -> @expressive = require '../lib/expressive'
  Given -> @app = express()
  Given -> @use = (req, res, next) =>
    next()
  Given -> @get = (req, res, next) =>
    res.status(200).end()
  Given -> sinon.spy @, 'use'
  Given -> sinon.spy @, 'get'

  describe 'with no current env defaults to process.env.NODE_ENV', ->
    Given -> process.env.NODE_ENV = 'development'
    Given -> @expressive(@app).env('development')
    Given -> @app.development.use '/foo', @use
    Given -> @app.get('/foo', @get)
    When (done) ->
      request(@app).get('/foo').end (err, res, body) -> done()
      return
    Then -> expect(@use).to.be.have.been.called

  describe 'with a current env', ->
    Given -> @expressive(@app, 'development').env('development')
    Given -> @app.development.use '/foo', @use
    Given -> @app.get('/foo', @get)
    When (done) ->
      request(@app).get('/foo').end (err, res, body) -> done()
      return
    Then -> expect(@use).to.be.have.been.called

  describe 'with a non matching env', ->
    Given -> @expressive(@app, 'test').env('development')
    Given -> @app.development.use '/foo', @use
    Given -> @app.get('/foo', @get)
    When (done) ->
      request(@app).get('/foo').end (err, res, body) -> done()
      return
    Then -> expect(@use).not.to.be.have.been.called

  describe 'with an alias', ->
    Given -> @expressive(@app, 'development').env('development', 'dev')
    Given -> @app.dev.use '/foo', @use
    Given -> @app.get('/foo', @get)
    When (done) ->
      request(@app).get('/foo').end (err, res, body) -> done()
      return
    Then -> expect(@use).to.be.have.been.called
