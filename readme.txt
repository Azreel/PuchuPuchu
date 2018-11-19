--------------------------------
  ぷ○ぷよ風落ち物パズルゲーム
 「ぷちゅぷちゅ」

   by SLP_GMV
--------------------------------

【ベース(参考)】
Java ぷよぷよ - Qiita - https://qiita.com/lboavde1121/items/ab7f89e05f193f183d78

【変更点】
ほぼ1から書き直したため、全てが変更点です。
画面サイズ、キー処理の一部のみベースを参考としています。

【動作環境】
Windows 10でJava 10を利用して開発を行っています。
一応、macOS 10.13.6でJava 8を利用して動作することも確認しています。

【操作方法】
←↓→キー --- ぷちゅ(顔)の移動
Zキー --- 左回転
Xキー --- 右回転
Qキー --- 終了(プレイ画面でのみ動作)
Pキー --- ポーズ(1Pプレイでのみ動作)

【遊び方】
同じぷちゅ(顔)を3つ繋げて消していきます。
ぷちゅ(顔)を消すことでスコアが加算され、その加算量は連続で消える連鎖によって増えていきます。
2Pプレイ時は消したぷちゅ(顔)に応じて相手に"おじゃまぷちゅ"を送って邪魔することができます。
おじゃまぷちゅは隣にならんでいるぷちゅ(顔)を消すと、一緒に消すことができます。
フィールドの中央上部にある「×」の位置までぷちゅ(顔)を積み上げてしまうとゲームオーバーです。

【2Pプレイに関して】
接続時はどちらか一方のプレイヤーがもう一方のPCのIPアドレスを指定することで接続を行ってください。
接続を受けた側はメッセージを1分以内に閉じなければ、強制切断と判定されます。
同期によって相手のフィールドのぷちゅが着地した際にぷちゅが瞬間移動することがありますが、仕様です。

【参考にしたサイト】
 [コード]
論理フォントを使用する - Fontクラス - Swing - https://www.javadrive.jp/tutorial/font/index2.html
Javaを用いたソケット通信　前編 - Qiita - https://qiita.com/akatsubaki/items/aaca7664c5b881415bf5
Javaを用いたソケット通信　後編 - Qiita - https://qiita.com/akatsubaki/items/ceb935f16c3402d3144d
Java: ローカルホスト名とIPアドレスを取得 - InetAddress.getLocalHost()メソッド - Yukun's Blog - https://yukun.info/java-inetaddress-getlocalhost/
警告ダイアログを表示する - JOptionPaneクラス - Swing - https://www.javadrive.jp/tutorial/joptionpane/index2.html
【Java】WAVファイルの再生 | のんぽぐ - https://nompor.com/2017/12/14/post-128/
Java の標準入力で、最後に立ち止まってしまうあなたに - Qiita - https://qiita.com/jiz/items/b7d65fb929afd24287d4
[java] Graphics2D.drawStringの改行に関する問題 [newline] | CODE Q&A 問題解決 [日本語] - https://code.i-harness.com/ja/q/4356cc
java - Anti-aliased JLabel - Stack Overflow - https://stackoverflow.com/questions/21869693/anti-aliased-jlabel
BufferedImage.subimageメソッド - ゆるゆるプログラミング - http://talavax.com/subimage.html
2点間の距離と角度と座標の求め方 - Qiita - https://qiita.com/Hoshi_7/items/d04936883ff3eb1eed2d
Algorithm of Puyo Connection - http://www.geocities.co.jp/Playtown/6524/connect.html
得点計算 - ぷよぷよ用語辞典 - アットウィキ - https://www26.atwiki.jp/puyowords/pages/122.html
 [リソース]
 