
class FanPod
{
  Str podName
  File podDir
  
  Version podVersion := Version("1.0")
  Str:Obj podIndex := [:]
  Str podSummary := ""
  Uri[] podSrc := [`fan/`]
  Uri[] javaDirs := [,]
  
  const Str:Str meta := [:]
  
  new make(Str name, Str buildFanPath) {
    podName = name
    podDir = File(Uri.fromStr(buildFanPath))
  }
  
  FanPod version(Str version) 
  { 
    podVersion = Version(version)
    return this
  }
  
  FanPod index(Str index, Obj obj) 
  { 
    podIndex[index] = obj
    return this
  }
  
  FanPod summary(Str summary) 
  { 
    podSummary = summary
    return this
  }
  
  FanPod src(Str[] srcs)
  {
    podSrc = srcs.map |Str src->Uri| { return Uri.fromStr(src) }
    return this
  }
  
  File baseDir() { podDir }
}
