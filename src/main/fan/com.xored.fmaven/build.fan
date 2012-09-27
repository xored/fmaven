using build

class Build : build::BuildPod
{
  new make()
  {
    podName = "fmaven"
    summary = "fmaven"
    depends = ["sys 1.0", "compiler 1.0"]
    srcDirs = [`fan/`, `fan/compiler/`]
  }
}
