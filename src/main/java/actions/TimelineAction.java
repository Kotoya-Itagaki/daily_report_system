package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.FollowService;
import services.LikeService;
import services.ReportService;

/**
 * タイムラインに関する処理を行うActionクラス
 *
 */
public class TimelineAction extends ActionBase {

    private ReportService service;
    private LikeService likeService;
    private FollowService followService;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {
        service = new ReportService();
        likeService = new LikeService();
        followService = new FollowService();

        //メソッドを実行
        invoke();

        service.close();
        likeService.close();
        followService.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();
        List<ReportView> reports = followService.getFollowReportPerPage(ev,page);

        //全日報データの件数を取得
        long reportsCount = followService.countReport(ev);

        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, reportsCount); //全ての日報データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //一覧画面を表示
        forward(ForwardConst.FW_TL_INDEX);
    }

}
