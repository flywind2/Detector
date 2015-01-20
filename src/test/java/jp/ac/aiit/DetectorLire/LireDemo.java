package jp.ac.aiit.DetectorLire;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.aiit.Detector.DetectorResult;
import jp.ac.aiit.Detector.util.Tool;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class LireDemo {

	// 分析対象フォルダ
	private String TARGET_DIR;

	// 一時中間データ保存フォルダ
	private String INDEX_PATH;
	
	// 画像違い度（0になたら、完全に類似）
	public int DIFF_LEVEL = 15;
		
	public DetectorResult search() throws IOException {

		TARGET_DIR = Tool.getResourcePath("/image");
		INDEX_PATH = Tool.getResourcePath("/index");

		DetectorResult ret = new DetectorResult();

        // 処理開始時間を取得します
        //long startTime = System.currentTimeMillis();
        // グループ計数
		int count = 0;
		// 分析対象ファイル数
		int indexCount = 0;
		// キャッシュ存在するかどうか確認
		boolean existsCacheFlg = false;
		
	    IndexReader reader = null;
	    ImageSearcher searcher = null;
		BufferedImage biImg = null;
		ImageSearchHits hits = null;
		List<String> fileNameList = new ArrayList<String>();
		Map<String, File> fileMap = new HashMap<String, File>();
		
		// 分析対象フォルダについて分析中間データ作成
		existsCacheFlg = DirectoryReader.indexExists(FSDirectory.open(new File(INDEX_PATH)));
		if (!existsCacheFlg) {
			indexCount = DetectorUtil.imageIndexing(INDEX_PATH, TARGET_DIR, true);
		}
		//System.out.println(useCacheFlg);
		//System.out.println(indexCount);
		// 重複抽出準備
		if (indexCount > 0 || existsCacheFlg) {
			File[] fileLists = new File(TARGET_DIR).listFiles();
			for (File f : fileLists) {
				if (f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".JPG")) {
					fileNameList.add(f.getName());
					fileMap.put(f.getName(), f);
				}
			}
			
		    try {
				reader = IndexReader.open(FSDirectory.open(new File(INDEX_PATH)));
				searcher = ImageSearcherFactory.createCEDDImageSearcher(50);
				
				while(fileNameList.size() > 0) {
					File obj = (File)fileMap.get(fileNameList.get(0));
					biImg = DetectorUtil.loadImage(obj);	
					hits = searcher.search(biImg, reader);

					for (int i = 0; i < hits.length(); i++) {
						// 類似度より抽出する
						if (hits.score(i) <= DIFF_LEVEL) {
							String filepath = hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
							fileNameList.remove((new File(filepath)).getName());
							ret.put(obj.getAbsolutePath(), (new File(filepath)).getAbsolutePath(), (double)hits.score(i));
						}
					}
					count++;	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		}
		
        // 処理終了時間を取得します
        //long endTime = System.currentTimeMillis();
        
        // 処理終了時間から処理開始時間を差し引いてミリ秒で処理時間を表示します
        //System.out.println("処理時間：" + (endTime - startTime)  + "ms");

		return ret;
	}

}
