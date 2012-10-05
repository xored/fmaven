**
** Compiler Tests
** 
class CompilerTests : Test
{
  Uri? podDir := unzip(`/resources/tests/pods.zip`)
  
  Void testWorld()
  {
    verifyNotNull(compile(FanPod("hello", unzip(`/resources/tests/hello.zip`)).depend("sys 1.0")))
  }
  
  Void testDependentWorld()
  {
    verifyNotNull(compile(FanPod("helloDep", unzip(`/resources/tests/helloDep.zip`)).depend("sys 1.0").depend("hello 1.0")))
  }
  
  File? compile(FanPod pod) 
  {
    compiler := Compiler(pod.podDir.uri, podDir) 
    {
      pods = [ pod ]
    }
    errors := compiler.compilePods
    compiler.dispose
    if (!errors.isEmpty) {
      fail(errors.toStr)
      return null;
    }
    podFile := pod.podDir.plus(Uri.fromStr(pod.podName + ".pod"));
    return podFile.exists ? podFile : null
  }
  
  Uri unzip(Uri uri)
  {
    outDir := tempDir.createDir(uri.basename)
    zipFile := Zip.read(typeof.pod.file(uri).in)
    
    File? file
    while ((file = zipFile.readNext) != null) 
    {
      if (!file.isDir)
      {
        file.copyTo(outDir.createFile(file.path.join("/")), ["overwrite":true])
      }
    }      
    outDir.deleteOnExit
    return outDir.uri
  }
}
