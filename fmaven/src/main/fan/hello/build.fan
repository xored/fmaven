class Build : build::BuildPod
{
  new make()
  {
    podName = "hello"
    summary = "hello world pod"
    depends = ["sys 1.0"]
    srcDirs = [`fan/`]
  }
}