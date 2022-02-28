# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.3.2] - 2022-02-28

### Fixed

- Send connect message immediately after ws session is open. 
This fixes hanging/stalled connections.

## [1.3.1] - 2022-01-05

### Added

- Expose all raw messages - see #23, #24.
- Implementing Livechat Realtime API

### Chore
- Updated dependencies

## 1.2.1 / 1.3.0
These releases are inconsistent and should not be used.

## [1.2.0] - 2021-10-04

### Added

- Adding room id to chat message to distinguish direct messages and channel messages - see #21. Thanks to @pjagielski and
  @dswiecki!

## [1.1.0] - 2021-09-09

### Added

- Get currently logged-in user id - see #17.

## [1.0.1] - 2020-06-17

### Fixed

- Handling of large messages.

### Added

- Support token and oauth login.
- Changelog :-)

## [0.1.2] - 2020-04-12

### Added

- Support for edited and reactions attributes in messages.
