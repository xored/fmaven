using f4parser 
using f4model

class Manifest
{
  File buildFan { private set }
  Str:Obj? vals { private set }
  
  const Str:Str meta := [:]
  const Depend[] rawDepends

  new make(Str arg) 
  { 
    buildFan = File(Uri.fromStr(arg))
    parser := Parser(buildFan.readAllStr, EmptyNamespace())
    MethodDef? method := parser.cunit.types.find { it.name.text == "Build" }?.slots?.find { it->name->text == "make" }
    if(method == null) throw ArgErr("Can't parse build.fan in $buildFan.pathStr")
    
    vals =  method.body.stmts
      .findAll { it is ExprStmt }
      .findAll |ExprStmt st->Bool| { st.expr.id == ExprId.assign }
      .map |ExprStmt st -> BinaryExpr| { st.expr}
      .reduce([Str:Obj?][:]) |Str:Obj? result, BinaryExpr expr->Str:Obj?|
      {
        if(expr.left isnot Ref) return result
        name := expr.left->text
        result[name] = resolveLiteral(expr.right)
        return result
      }
    
    rawDepends = Depend[,]
    rawDepends = depends.reduce(Depend[,]) |Depend[] r, Str raw -> Depend[]|
    {
      Depend? depend := null
      try { depend = Depend.fromStr(raw) } catch(Err e) {}
      if(depend != null)
        r.add(depend)
      return r
    }
  }
  
  Str? podName() { vals["podName"] }
  
  Version version() { vals["version"] ?: Version("1.0") }
  
  Str:Obj index() { vals["index"] ?: [Str:Obj][:] }
  
  Str summary() { vals["summary"] ?: "" }
  
  Uri? outDir() 
  { 
    vals["outPodDir"] 
  }
  
  Str[] depends() { vals["depends"] ?: Str[,] }
  
  Uri[] resDirs() { vals["resDirs"] ?: Uri[,] }
  
  Uri[] jsDirs() { vals["jsDirs"] ?: Uri[,] }
  
  Uri[] javaDirs() { vals["javaDirs"] ?: Uri[,] }
  
  File? baseDir() { buildFan.parent }
  
  Uri[] srcDirs() {  vals["srcDirs"] }

  
  //////////////////////////////////////////////////////////////////////////
  // Helper methods
  //////////////////////////////////////////////////////////////////////////
  private static Obj? resolveLiteral(Expr expr)
  {
    if (expr is Literal) return expr->val
    if (expr is ListLiteral)
    {
      list := expr as ListLiteral
      return list.items.map { resolveLiteral(it) }
    }
    if (expr is MapLiteral)
    {
      map := expr as MapLiteral
      result := [:]
      map.each |k, v|
      {
        result[resolveLiteral(k)] = resolveLiteral(v)
      }
      return result
    }
    if (expr is CallExpr)
    {
      call := expr as CallExpr
      // Possibly, more generic solution is needed
      
      if (((call.callee as InvokeExpr)?.callee as UnresolvedRef)?.text == "Version")
      {
        s := (call.args.getSafe(0) as Literal)?.val as Str
        if (s != null) return Version(s)
      }
    }
    return null
  }
  
  private Uri[] unfoldDirs(Uri[] dirs, Uri baseDir)
  {
    result := Uri[,]
    result.addAll(dirs)
    dirs.each |dir|
    {
      fullPath := (baseDir + dir).toFile
      result.addAll(
        unfoldDirs(
          fullPath.listDirs
          .map { it.uri.relTo(baseDir)}
          , baseDir
        )
      )
    }
    return result
  }
}
