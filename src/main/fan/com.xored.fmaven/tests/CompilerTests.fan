**
** Compiler Tests
** 
class CompilerTests : Test
{
  
  Void testHelloWorld()
  {
    verifyNotNull(compile(unzip(`/resources/tests/hello.zip`)))
  }
  
  
  File? compile(File dir) 
  {
     errors := Compiler(dir.uri) 
    {
      manifests = 
      [
        FanPod(dir.basename, dir.uri)
      ]
    }.compileManifests
    if (!errors.isEmpty) {
      return null;
    }
    podFile := dir.plus(Uri.fromStr(dir.basename + ".pod"));
    return podFile.exists ? podFile : null
  }
  
  File unzip(Uri uri)
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
    return outDir
  }
}
