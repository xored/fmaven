using fcompiler

class FMavenNamespace : CNamespace
{
  private static const Str podExt := "pod"
  
  private const Str:File locs
  
  new make(Str:File locs := [:]) 
  {
    this.locs = locs
    init
  }
  
  override FPod? findPod(Str podName)
  {
    if(!locs.containsKey(podName)) return null
    loc := locs[podName]
    if(!loc.exists) return null
    fpod := FPod(this, podName, Zip.open(loc))
    fpod.read
    return fpod
  }
  
  private Zip addZip(Zip zip) {
    zips.add(zip)
    return zip
  }
  private Zip[] zips := [,]
  
  Void addPod (Str loc, File file) 
  { 
    if (!podExt.equalsIgnoreCase(file.ext)) { return }
    locs.add(loc, file) 
  }
  
  public Void close() {
    zips.each { it.close }
  }
}