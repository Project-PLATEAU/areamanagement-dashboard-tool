package view3d.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.util.IOUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import view3d.entity.Layer;
import view3d.entity.PostLayerAttribute;
import view3d.entity.PostLayerFeature;
import view3d.entity.PostLayerIconPath;
import view3d.entity.ResponseError;
import view3d.entity.Theme;
import view3d.form.AttachmentForm;
import view3d.form.DeletePostLayerForm;
import view3d.form.LayerForm;
import view3d.form.LayerSourceForm;
import view3d.form.PostLayerAttributeForm;
import view3d.form.PostLayerFeatureForm;
import view3d.form.PostSearchForm;
import view3d.form.ResponseEntityForm;
import view3d.form.ThemeLayerForm;
import view3d.service.AttachmentsService;
import view3d.service.GraphListService;
import view3d.service.LayerService;
import view3d.service.LayerSourceFieldService;
import view3d.service.LayerSourceService;
import view3d.service.PostLayerIconPathService;
import view3d.service.PostLayerService;
import view3d.service.ThemeLayerService;
import view3d.service.ThemeService;
import view3d.util.AuthUtil;

@RestController
@RequestMapping("/layers")
public class LayersController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LayersController.class);

	@Autowired
	LayerService layerService;
	
	@Autowired
	LayerSourceService layerSourceService;
	
	@Autowired
	LayerSourceFieldService layerSourceFieldService;
	
	@Autowired
	ThemeLayerService themeLayerService;
	
	@Autowired
	PostLayerService postLayerService;
	
	@Autowired
	AttachmentsService attachmentsService;
	
	@Autowired
	ThemeService themeService;
	
	@Autowired
	GraphListService graphListService;
	
	@Autowired
	PostLayerIconPathService postLayerIconPathService;

	@Value("${app.file.rootpath}")
	protected String fileRootPath;
	
	@Value("${app.billboard.icons.rootpath}")
	protected String iconRootPath;
	
	@Value("${app.billboard.layer.icon.rootpath}")
	protected String layerIconRootPath;
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)レイヤ一覧取得", notes = "全てのレイヤ一覧を取得")
	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<LayerForm> getAllLayers() {
		List<LayerForm> layerFormList =  null;
		try {
			layerFormList =  layerService.findAll();
		} catch (Exception e) {
			LOGGER.error("レイヤ一覧取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(layerFormList == null) {
			LOGGER.error("レイヤ一覧情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return layerFormList;
	}
	
	@RequestMapping(value = "/getAllPostAndActivityLayer", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)レイヤ一覧取得", notes = "エリマネ活動及び投稿レイヤのレイヤ一覧を取得")
	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<LayerForm> getAllPostAndActivityLayer() {
		List<LayerForm> layerFormList =  null;
		try {
			layerFormList =  layerService.findAllPostAndActivityLayer();
		} catch (Exception e) {
			LOGGER.error("レイヤ一覧取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(layerFormList == null) {
			LOGGER.error("レイヤ一覧情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return layerFormList;
	}
	
	@RequestMapping(value = "/{themeId}", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)レイヤ一覧取得", notes = "themeIdに紐づくレイヤ一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeLayerForm> getLayersByThemeId(@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,@CookieValue(value = "token", required = false) String token) {
		List<ThemeLayerForm> themeLayerFormList =  null;
		Theme theme = themeService.findByThemeId(themeId);
		String role = AuthUtil.getRole(token);
		if(!"1".equals(theme.getPublishFlag()) && !"admin".equals(role)) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			themeLayerFormList =  themeLayerService.findByThemeId(themeId);
		} catch (Exception e) {
			LOGGER.error("レイヤ一覧取得に失敗 themeId： " + themeId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeLayerFormList == null) {
			LOGGER.error("レイヤ一覧情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeLayerFormList;
	}
	
	@RequestMapping(value = "/{themeId}", method = RequestMethod.POST)
	@ApiOperation(value = "(権限制御あり)レイヤ一覧取得", notes = "レイヤ設定を切替項目で置き換えたthemeIdに紐づくレイヤ一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeLayerForm> getLayersByThemeIdAndSwitchItem(@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,
			@ApiParam(required = true, value = "切替項目 {切替項目名：切替項目値} ")@RequestBody Map<String, String> switchItemMap,@CookieValue(value = "token", required = false) String token) {
		List<ThemeLayerForm> themeLayerFormList =  null;
		Theme theme = themeService.findByThemeId(themeId);
		String role = AuthUtil.getRole(token);
		if(!"1".equals(theme.getPublishFlag()) && !"admin".equals(role)) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			themeLayerFormList =  themeLayerService.findByThemeId(themeId,switchItemMap);
		} catch (Exception e) {
			LOGGER.error("レイヤ一覧取得に失敗 themeId： " + themeId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeLayerFormList == null) {
			LOGGER.error("レイヤ一覧情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeLayerFormList;
	}
	
	@RequestMapping(value = "/updateThemeLayer/{themeId}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)テーマ情報更新", notes = "themeIdに紐づくテーマ情報を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエストが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public List<ThemeLayerForm> updateThemeLayer(@ApiParam(required = true, value = "テーマフォーム")@RequestBody @Validated List<ThemeLayerForm> themeLayerFormList,
			@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,
			@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("テーマ情報の更新開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			List<ThemeLayerForm> result = themeLayerService.update(themeLayerFormList, themeId);
			if(result != null) {
				LOGGER.info("テーマ情報の更新完了");
				return result;
			}else {
				throw new Exception("processing error");
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("テーマ情報の更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@RequestMapping(value = "/deleteThemeLayer/{themeId}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)テーマレイヤ情報削除", notes = "themeIdに紐づくテーマレイヤ情報を削除")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 201, message = "処理成功", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm deleteThemeLayer(@ApiParam(required = true, value = "テーマフォーム")@RequestBody @Validated List<ThemeLayerForm> themeLayerFormList,
			@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,
			@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("テーマ情報の削除開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			boolean result = themeLayerService.delete(themeLayerFormList, themeId);
			if(result == true) {
				LOGGER.info("テーマ情報の削除完了");
				ResponseEntityForm responseEntityForm = new ResponseEntityForm(HttpStatus.CREATED.value(),
						"テーマレイヤ情報削除完了");
				return responseEntityForm;
			}else {
				throw new Exception("processing error");
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("テーマ情報の削除に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getPostLayer", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)投稿レイヤを１件取得", notes = "featureIdに紐づく投稿レイヤを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public PostLayerFeatureForm getPostLayerByFeatureId(@ApiParam(required = true, value = "フィーチャID")@RequestParam("featureId") Integer featureId,@CookieValue(value = "token", required = false) String token) {
		PostLayerFeatureForm postLayerFeatureForm = null;
		try {
			postLayerFeatureForm = postLayerService.getPostLayer(featureId);
		} catch (Exception e) {
			LOGGER.error("投稿レイヤの取得に失敗 featureId： " + featureId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(postLayerFeatureForm == null || postLayerFeatureForm.getFeatureId() == null) {
			LOGGER.error("投稿レイヤ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		//閲覧権限チェックを実施
		if(!AuthUtil.postViewAuthorityCheck(token, postLayerFeatureForm.getPostUserId(), postLayerFeatureForm.getPublishFlag())) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return postLayerFeatureForm;
	}
	
	@RequestMapping(value = "/admin/getPostLayer/{layerId}", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)投稿レイヤフィーチャ一覧取得", notes = "layerIdに紐づく投稿レイヤフィーチャ一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<PostLayerFeatureForm> getPostLayerByLayerId(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId) {
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		try {
			postLayerFeatureFormList = postLayerService.getPostLayerListByLayerId(layerId);
		} catch (Exception e) {
			LOGGER.error("投稿レイヤの取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(postLayerFeatureFormList == null || postLayerFeatureFormList.size() < 1) {
			LOGGER.error("投稿レイヤの取得に失敗");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return postLayerFeatureFormList;
	}
	
	@RequestMapping(value = "/admin/search/{layerId}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)投稿レイヤフィーチャ一覧取得", notes = "検索データに該当する投稿レイヤフィーチャ一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<PostLayerFeatureForm> getPostLayerByLayerIdAndSearchData(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId,@ApiParam(required = true, value = "投稿検索フォーム")@RequestBody @Validated PostSearchForm postSearchForm) {
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		try {
			postLayerFeatureFormList = postLayerService.getPostLayerListByLayerIdAndSearchData(layerId,postSearchForm);
		} catch (Exception e) {
			LOGGER.error("投稿レイヤの検索に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(postLayerFeatureFormList == null || postLayerFeatureFormList.size() < 1) {
			LOGGER.error("投稿レイヤの取得に失敗");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return postLayerFeatureFormList;
	}
	
	@RequestMapping(value = "/postLayerAttribute", method = RequestMethod.GET)
	@ApiOperation(value = "投稿レイヤ属性取得", notes = "themeIdに紐づく投稿レイヤの属性情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public Map<String, Object> getPostLayerAttribute(@ApiParam(required = true, value = "テーマID")@RequestParam("themeId") Integer themeId){
		try {
			final Map<String, Object> res = new HashMap<>();
			final Layer layerInfo = postLayerService.getPostLayerInfo(themeId);
			res.put("layerInfo", layerInfo);
			final List<PostLayerAttribute> PostLayerAttribute =  postLayerService.getPostLayerAttribute(themeId);
			res.put("attribute", PostLayerAttribute);
			return res;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("投稿レイヤ属性の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/postLayerAttribute/{layerId}", method = RequestMethod.GET)
	@ApiOperation(value = "投稿レイヤ属性取得", notes = "layerIdに紐づく投稿レイヤの属性情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<PostLayerAttributeForm> getPostLayerAttributeOnLayerId(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId) {
		List<PostLayerAttributeForm> postLayerAttributeFormList = null;
		try {
			postLayerAttributeFormList = postLayerService.getPostLayerAttributeByLayerIdOrderByDispOrder(layerId);
		} catch (Exception e) {
			LOGGER.error("投稿レイヤフィーチャ属性の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(postLayerAttributeFormList == null || postLayerAttributeFormList.size() < 1) {
			LOGGER.error("投稿レイヤフィーチャ属性の取得に失敗");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return postLayerAttributeFormList;
	}
	
	@RequestMapping(value = "/admin/register/{layerId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)レイヤ情報を更新", notes = "layerIdに紐づくレイヤを更新")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public Layer registerLayer(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId, @ApiParam(required = true, value = "レイヤフォーム")@RequestBody @Validated LayerForm layerForm,@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("レイヤの更新開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			Layer layer = layerService.update(layerId, layerForm);
			LOGGER.info("レイヤの更新終了");
			return layer;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("レイヤの更新に失敗 layerId： " + layerForm.getLayerId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/registerAttribute/{layerId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)レイヤの属性情報を更新", notes = "layerIdに紐づくレイヤの属性情報を更新")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<PostLayerAttributeForm> registerFeatureAttribute(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId, @ApiParam(required = true, value = "フォーム")@RequestBody @Validated List<PostLayerAttributeForm> postLayerAttributeFormList,@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("属性情報の更新開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			//投稿レイヤの件数確認してデータ型が登録可能かの確認をする。
			List<PostLayerFeatureForm> postLayerFeatureFormList = postLayerService.getPostLayerListByLayerId(layerId);
			boolean flag = false;
			if(postLayerFeatureFormList != null) {
				flag = true;
			}
			LayerSourceForm layerSourceForm = layerSourceService.findByLayerId(layerId);
			//レイヤソースフィールド更新時のグラフ・リスト情報のエイリアス名を更新
			graphListService.updateGraphListForLayerSourceField(layerSourceForm.getSourceId(), postLayerAttributeFormList);
			layerSourceFieldService.update(layerSourceForm.getSourceId(), postLayerAttributeFormList);
			List<PostLayerAttributeForm> result = postLayerService.updateAttribute(postLayerAttributeFormList, flag);
			if(result != null) {
				LOGGER.info("テーマ情報の更新完了");
				return result;
			}else {
				throw new Exception("processing error");
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("属性情報の更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(権限制御あり)投稿レイヤフィーチャを登録・更新", notes = "featureIdに紐づく投稿レイヤフィーチャが存在しない場合は登録、存在する場合は更新")
	@ResponseBody
	@ApiResponses(value = {	@ApiResponse(code = 202, message = "更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public PostLayerFeatureForm registerFeature(@ApiParam(required = true, value = "投稿レイヤフィーチャフォーム")@RequestBody @Validated PostLayerFeatureForm postLayerFeatureForm,@ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {
		LOGGER.info("投稿レイヤフィーチャの登録・更新開始");
		if (erros.hasErrors()) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		PostLayerFeature registerFeature = new PostLayerFeature();
		Optional<Integer> idOps = Optional.ofNullable(postLayerFeatureForm.getFeatureId());
		if (idOps.isPresent() && idOps.get() != 0) {
			Optional<PostLayerFeature> featureOpt = postLayerService.findByFeatureIdForEntity(idOps.get());
			if (featureOpt.isPresent()) {
				registerFeature = featureOpt.get();
				//更新権限チェックを実施
				if(!AuthUtil.postUpdateAuthorityCheck(token, registerFeature.getPostUserId(), registerFeature.getPublishFlag())) {
					LOGGER.error("権限エラー");
					throw new ResponseStatusException(HttpStatus.FORBIDDEN);
				}
				try {
				String role = AuthUtil.getRole(token);
				//公開済みの投稿を管理者が編集した場合は公開済みのまま更新する
				if("1".equals(registerFeature.getPublishFlag()) &&  ("admin".equals(role) || "erimane".equals(role))) {
					postLayerFeatureForm.setPublishFlag("1");
				}else {
					postLayerFeatureForm.setPublishFlag("0");
				}
				postLayerService.updateFeature(registerFeature, postLayerFeatureForm);
				} catch (Exception e) {
					LOGGER.error("投稿レイヤフィーチャの登録・更新に失敗 featureId： " + postLayerFeatureForm.getFeatureId());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.ACCEPTED);
			}
		} else {
			//登録権限チェックを実施
			if(!AuthUtil.postRegisterAuthorityCheck(token)) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			try {
				postLayerFeatureForm = postLayerService.insert(postLayerFeatureForm);
			} catch (Exception e) {
				LOGGER.error("投稿レイヤフィーチャの登録・更新に失敗 featureId： " + postLayerFeatureForm.getFeatureId());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		LOGGER.info("投稿レイヤフィーチャの登録・更新終了");
		return postLayerFeatureForm;
	}
	
	@RequestMapping(value = "/attachments/upload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ApiOperation(value = "(権限制御あり)添付ファイルのアップロード", notes = "添付ファイルをDB/サーバ上に登録")
	@ResponseBody
	@ApiResponses(value = {	@ApiResponse(code = 201, message = "処理成功", response = ResponseError.class),
			@ApiResponse(code = 202, message = "投稿情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm uploadAttachmentFile(@ApiParam(required = true, value = "添付ファイルフォーム")@ModelAttribute AttachmentForm attachmentForm, @ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {

		LOGGER.info("添付ファイルのアップロード開始");
		if (erros.hasErrors() || attachmentForm.getFeatureId() == null) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		String fileName = attachmentForm.getAttachmentFileName();
		int point = fileName.lastIndexOf(".");
		if (!"jpg".equals(fileName.substring(point + 1)) 
			&& !"jpeg".equals(fileName.substring(point + 1))
			&& !"JPG".equals(fileName.substring(point + 1))
			&& !"JPEG".equals(fileName.substring(point + 1)) 
			&& !"png".equals(fileName.substring(point + 1))
			&& !"PNG".equals(fileName.substring(point + 1)) 
			&& !"tif".equals(fileName.substring(point + 1)) 
			&& !"tiff".equals(fileName.substring(point + 1))
			&& !"TIF".equals(fileName.substring(point + 1))
			&& !"TIFF".equals(fileName.substring(point + 1))) {
			LOGGER.warn("リクエストエラー(形式不整合)");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		//偽装対策用にMultipartFileからのファイル名もチェックする
		fileName = attachmentForm.getUploadFile().getOriginalFilename();
		point = fileName.lastIndexOf(".");
		if (!"jpg".equals(fileName.substring(point + 1)) 
				&& !"jpeg".equals(fileName.substring(point + 1))
				&& !"JPG".equals(fileName.substring(point + 1))
				&& !"JPEG".equals(fileName.substring(point + 1)) 
				&& !"png".equals(fileName.substring(point + 1))
				&& !"PNG".equals(fileName.substring(point + 1)) 
				&& !"tif".equals(fileName.substring(point + 1)) 
				&& !"tiff".equals(fileName.substring(point + 1))
				&& !"TIF".equals(fileName.substring(point + 1))
				&& !"TIFF".equals(fileName.substring(point + 1))) {
			LOGGER.warn("リクエストエラー(形式不整合)");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		PostLayerFeatureForm postLayerFrature = postLayerService.getPostLayer(attachmentForm.getFeatureId());

		if (postLayerFrature != null) {
			//更新権限チェックを実施
			if(!AuthUtil.postUpdateAuthorityCheck(token, postLayerFrature.getPostUserId(), postLayerFrature.getPublishFlag())) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			attachmentsService.uploadFeature(attachmentForm);
			ResponseEntityForm responseEntityForm = new ResponseEntityForm(HttpStatus.CREATED.value(),
					"Attachment File registration successful.");
			return responseEntityForm;
		} else {
			LOGGER.info("アップロード対象の活動情報が存在しない　activityId: "+attachmentForm.getActivityId());
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
	}
	
	@RequestMapping(value = "/attachments/feature_{featureId}/{fileName}", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)添付ファイルを表示", notes = "featureIdとfileNameで添付ファイルを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 202, message = "投稿が存在しない場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public HttpEntity<byte[]> getAttachmentFile(@ApiParam(required = true, value = "フィーチャID")@PathVariable("featureId") Integer featureId,
			@ApiParam(required = true, value = "ファイル名")@PathVariable("fileName") String fileName,@CookieValue(value = "token", required = false) String token) {
		//閲覧権限チェックを実施
		PostLayerFeatureForm postLayerFrature = postLayerService.getPostLayer(featureId);
		if (postLayerFrature == null || postLayerFrature.getFeatureId() == null) {
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
		if(!AuthUtil.postViewAuthorityCheck(token, postLayerFrature.getPostUserId(), postLayerFrature.getPublishFlag())) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			// 絶対ファイルパス
			String absoluteFilePath = fileRootPath + "feature_" + featureId + "/" + fileName;
			LOGGER.debug(absoluteFilePath);
			Path filePath = Paths.get(absoluteFilePath);
			if (!Files.exists(filePath)) {
				// ファイルが存在しない
				LOGGER.warn("ファイルが存在しない");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			// リソースファイルを読み込み
			File file = new File(absoluteFilePath);
			InputStream is = new FileInputStream(file);
			// byteへ変換
			byte[] data = IOUtils.toByteArray(is);
			// レスポンスデータとして返却
			is = new BufferedInputStream(new ByteArrayInputStream(data));
			String mimeType = null;
			int point = fileName.lastIndexOf(".");
			if ("jpg".equals(fileName.substring(point + 1)) || "jpeg".equals(fileName.substring(point + 1))
					|| "JPG".equals(fileName.substring(point + 1))
					|| "JPEG".equals(fileName.substring(point + 1))) {
				mimeType = "image/jpeg";
			} else if ("png".equals(fileName.substring(point + 1))
					|| "PNG".equals(fileName.substring(point + 1))) {
				mimeType = "image/png";
			} else if ("tif".equals(fileName.substring(point + 1)) || "tiff".equals(fileName.substring(point + 1))
					|| "TIF".equals(fileName.substring(point + 1))
					|| "TIFF".equals(fileName.substring(point + 1))) {
				mimeType = "image/tiff";
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", mimeType);
			headers.setContentLength(data.length);
			LOGGER.info("添付ファイルの取得:" + absoluteFilePath);
			return new HttpEntity<byte[]>(data, headers);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("添付ファイルの取得に失敗 featureId： " + featureId + " , fileName: "+ fileName);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@RequestMapping(value = "/billboard/iconImage", method = RequestMethod.GET)
	@ApiOperation(value = "投稿レイヤのbillboardアイコンを取得", notes = "featureIdで対応する属性値のbillboardアイコンを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public HttpEntity<byte[]> getIconImage(@ApiParam(required = true, value = "フィーチャーID")@RequestParam("featureId") Integer featureId,@ApiParam(required = true, value = "アイテムID")@RequestParam("itemId") Integer itemId) {
		try {
			// アイコンパス
			String absoluteIconPath = iconRootPath + "sample-icon.png";
			PostLayerFeatureForm postLayerFratureForm = postLayerService.getPostLayer(featureId);
			if(postLayerFratureForm != null && postLayerFratureForm.getFeatureId() != null) {
				//判定対象のアイテム項目値をセット
				String judgementValue = "";
				switch(itemId) {
					case 1:
						judgementValue = postLayerFratureForm.getItem1();
						break;
					case 2:
						judgementValue = postLayerFratureForm.getItem2();
						break;
					case 3:
						judgementValue = postLayerFratureForm.getItem3();
						break;
					case 4:
						judgementValue = postLayerFratureForm.getItem4();
						break;
					case 5:
						judgementValue = postLayerFratureForm.getItem5();
						break;
					case 6:
						judgementValue = postLayerFratureForm.getItem6();
						break;
					case 7:
						judgementValue = postLayerFratureForm.getItem7();
						break;
					case 8:
						judgementValue = postLayerFratureForm.getItem8();
						break;
					case 9:
						judgementValue = postLayerFratureForm.getItem9();
						break;
					case 10:
						judgementValue = postLayerFratureForm.getItem10();
						break;
					default:
						break;
				}
				List<PostLayerIconPath> postLayerIconPathList = postLayerIconPathService.findByLayerIdAndJudgmentValue(postLayerFratureForm.getLayerId(), judgementValue);
				for(PostLayerIconPath postLayerIconPath : postLayerIconPathList) {
					String iconPath = postLayerIconPath.getImagePath();
					if(iconPath.startsWith("/") && iconRootPath.endsWith("/")) {
						iconPath = iconPath.replaceFirst("/", "");
					}
					absoluteIconPath = iconRootPath + postLayerIconPath.getImagePath();
				}
			}
			LOGGER.debug(absoluteIconPath);
			Path filePath = Paths.get(absoluteIconPath);
			if (!Files.exists(filePath)) {
				// ファイルが存在しない
				LOGGER.warn("ファイルが存在しない");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			// リソースファイルを読み込み
			File file = new File(absoluteIconPath);
			InputStream is = new FileInputStream(file);
			// byteへ変換
			byte[] data = IOUtils.toByteArray(is);
			// レスポンスデータとして返却
			is = new BufferedInputStream(new ByteArrayInputStream(data));
			String mimeType = "image/png";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", mimeType);
			headers.setContentLength(data.length);
			LOGGER.info("添付ファイルの取得:" + absoluteIconPath);
			return new HttpEntity<byte[]>(data, headers);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("添付ファイルの取得に失敗 featureId： " + featureId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ApiOperation(value = "(権限制御あり)投稿レイヤ情報を削除", notes = "投稿レイヤ情報を削除し新たなparentFeatureIdを取得")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 202, message = "投稿情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public DeletePostLayerForm deletePostLayer(@ApiParam(required = true, value = "投稿レイヤ削除フォーム")@RequestBody @Validated DeletePostLayerForm postLayerForm,@ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {
		LOGGER.info("投稿レイヤ情報の削除開始");
		if (erros.hasErrors()) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (postLayerForm.getFeatureId() > 0 && postLayerForm.getParentFeatureId() > 0) {
			//削除権限チェックを実施
			PostLayerFeatureForm postLayerFrature = postLayerService.getPostLayer(postLayerForm.getFeatureId());
			if (postLayerFrature == null || postLayerFrature.getFeatureId() == null) {
				throw new ResponseStatusException(HttpStatus.ACCEPTED);
			}
			if(!AuthUtil.postDeleteAuthorityCheck(token, postLayerFrature.getPostUserId(), postLayerFrature.getPublishFlag())) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			Integer newParentFeatureId = null;
			try {
				newParentFeatureId = postLayerService.delete(postLayerForm);
			} catch (Exception e) {
				LOGGER.error("投稿レイヤ情報の削除に失敗 featureId： " + postLayerForm.getFeatureId());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOGGER.info("投稿レイヤ情報の削除終了");
			return new DeletePostLayerForm(postLayerForm.getFeatureId(), newParentFeatureId);
		} else {
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
	}
	
	@RequestMapping(value = "/postLayer/publish", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)投稿レイヤフィーチャ公開設定更新", notes = "投稿レイヤフィーチャの公開設定情報を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "更新成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm updatePublish(@ApiParam(required = true, value = "投稿レイヤフィーチャリスト")@RequestBody List<PostLayerFeatureForm> postLayerFeatureFormList) {
		boolean result = false;
		try {
			//投稿情報公開設定を更新
			result = postLayerService.updatePublish(postLayerFeatureFormList);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				LOGGER.error("投稿情報公開設定更新に失敗");
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOGGER.error("投稿情報公開設定更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/layerSource/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)レイヤソース一覧取得", notes = "レイヤソース一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<LayerSourceForm> getlayerSource() {
		List<LayerSourceForm> layerSourceFormList =  null;
		try {
			layerSourceFormList =  layerSourceService.findAll();
		} catch (Exception e) {
			LOGGER.error("レイヤソース一覧取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(layerSourceFormList == null || layerSourceFormList.size() < 1) {
			LOGGER.error("レイヤソース一覧情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return layerSourceFormList;
	}

}
