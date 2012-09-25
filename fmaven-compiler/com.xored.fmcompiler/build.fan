using build
class Build : build::BuildPod
{
  new make()
  {
    podName = "fmcompiler"
    summary = "fmaven compiler"
    depends = ["sys 1.0", "compiler 1.0", "compilerJava 1.0", "concurrent 1.0", "f4parser 1.0", "f4model 1.0"]
    srcDirs = [`fan/`, `fan/compiler/`]
  }
}
