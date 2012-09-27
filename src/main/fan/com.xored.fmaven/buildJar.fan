using build

class Build : BuildScript
{
  @Target { help = "build fmaven pod as a single JAR dist" }
  Void distFansh()
  {
    dist := JarDist(this)
    dist.outFile = `./fmaven.jar`.toFile.normalize
    dist.podNames = Str["fmaven", "compiler"]
    dist.mainMethod = "fmaven::Main.main"
    dist.run
  }
}