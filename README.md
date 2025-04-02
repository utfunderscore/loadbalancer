<h1 align="center">
<img width="300px" src="logo.png" />

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
</h1>


## <a name="table-of-contents"></a> Table of Contents

1. [Features](#features)
2. [Roadmap](#roadmap)
3. [Building](#building)
## 1. <a name="features"></a> Features <small><sup>[Top ▲](#table-of-contents)</sup></small>
- Circular loadbalancing
  - Load balance using a pre-defined set of endpoints
- Http loadbalancing
  - Load balance using json responses from a http endpoint
## 2. <a name="roadmap"></a> Roadmap <small><sup>[Top ▲](#table-of-contents)</sup></small>
- [ ] Implement status checks for backend services

## 3. <a name="running"></a> Running <small><sup>[Top ▲](#table-of-contents)</sup></small>
```bash
java -Xmx500m -jar loadbalancer.jar
```

## 4. <a name="building"></a> Building <small><sup>[Top ▲](#table-of-contents)</sup></small>
```bash
./gradlew shadowJar
```

## 4. 
[contributors-shield]: https://img.shields.io/github/contributors/utfunderscore/loadbalancer.svg
[contributors-url]: https://github.com/utfunderscore/loadbalancer/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/utfunderscore/loadbalancer.svg
[forks-url]: https://github.com/utfunderscore/loadbalancer/network/members
[stars-shield]: https://img.shields.io/github/stars/utfunderscore/loadbalancer.svg
[stars-url]: https://github.com/utfunderscore/loadbalancer/stargazers
[issues-shield]: https://img.shields.io/github/issues/utfunderscore/loadbalancer.svg
[issues-url]: https://github.com/utfunderscore/loadbalancer/issues