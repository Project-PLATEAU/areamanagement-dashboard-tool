#
# jwttokenの認証フィルター
# 認証に失敗した場合はログイン画面を表示
# 静的ファイル及びmetabaseが対象
# 配置場所:/usr/local/apache2/conf/
#

# apacheのドキュメントルートを指定
LOCAL_ROOT_PATH = "/usr/local/apache2/htdocs"
# JWT署名検証用のシークレットkey(必ず発行元と同じ値にしてください)
SECRET_KEY = "secret"
# JWT token名(必ず発行元と同じ値にしてください)
TOKEN_NAME = "token"
# JWT サブジェクト名(必ず発行元と同じ値にしてください)
SUBJECT_NAME = "3dviewapi"
# metabaseとgeoserverのLOCAL URL
API_URL = {
    "metabase" => "http://localhost:3000/",
    "geoserver" => "http://localhost:8080/geoserver/"
}
jwtString = ""
begin
  r = Apache::Request.new
  hin = Apache::Headers_in.new
  if !r.uri.include?("/meta/") && !r.uri.include?("config.json") && !r.uri.include?("/login/") && !r.uri.include?("/logout/") && !r.uri.include?("/redirect/") then
    jwtArray = hin["Cookie"].split("; ")
    for var in jwtArray do
      if var.include?(TOKEN_NAME + "=")
        targetToken = var.split("; ")[0].split(", ")[0]
        jwtString = targetToken.sub(TOKEN_NAME + "=","")
        break
      end
    end
    if jwtString.empty? 
      raise StandardError.new("tokenが存在しない")
    end
    decoded = JWT.decode(jwtString, SECRET_KEY).first
    # tokenの認証
    if Time.now.to_i > decoded["exp"]
      raise StandardError.new("有効期限切れ")
    elsif SUBJECT_NAME != decoded["sub"]
      raise StandardError.new("subjectの不一致")
    end
  end
  # proxy処理
  result = 0
  API_URL.each do |k,v|
    if r.uri.include?("/" + k + "/")
      r.handler  = "proxy-server"
      r.proxyreq = Apache::PROXYREQ_REVERSE
      r.filename = "proxy:" + v + r.uri.sub("/" + k + "/", '')
      result = 1
      break
    end
  end
  if result == 0
    r.filename = LOCAL_ROOT_PATH + r.uri
  end
  Apache::return(Apache::OK)
rescue Exception => e
  r = Apache::Request.new
  r.filename = LOCAL_ROOT_PATH + "/login/index.html"
  Apache.return(Apache::OK)
end