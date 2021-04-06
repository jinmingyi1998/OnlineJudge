# How to create(update) the problem set:

## 1 Problem page: /admin (request administrator)

then add or update problem. Markdown & Katex are supported

## 2 Problem data:

Problem data is accessed by judger server, which is mounted in docker:/ojdata
```yaml
volumes:
      - c:\host\path:/ojdata  # on Windows docker engine
      - /host/path:/ojdata    # on Linux
```
A recommended way to operate data:

Update data directly in the directory mounted on the host.

The directory should be like this:
```
.
├── 1
│   ├── 1.in
│   ├── 1.out
│   ├── 2.in
│   ├── 2.out
│   ├── a.in
│   ├── a.out
│   ├── b.in
│   └── b.out
├── 2
│   ├── 1.in
│   ├── 1.out
│   ├── 2.in
│   ├── 2.out
│   ├── a.in
│   ├── a.out
│   ├── b.in
│   └── b.out
├── 3
│   ├── 1.in
│   ├── 1.out
│   ├── 2.in
│   ├── 2.out
│   ├── a.in
│   ├── a.out
│   ├── b.in
│   └── b.out
└── 4
    ├── 1.in
    ├── 1.out
    ├── 2.in
    ├── 2.out
    ├── a.in
    └── a.out
```
Directory name is problem id. and there are input files(*.in) and output files(*.out)

Also, there is a port 8000 expose by judger server docker for a python file viewer.
