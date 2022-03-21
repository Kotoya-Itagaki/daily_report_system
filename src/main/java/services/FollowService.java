package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.FollowConverter;
import actions.views.FollowView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Report;

public class FollowService extends ServiceBase {

    /**
     * 指定した従業員がフォローしている従業員の日報を、指定されたページ数分取得し、一覧画面に表示する
     * @param follow フォローした従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト ReportView
     */
    public List<ReportView> getFollowReportPerPage(EmployeeView follow, int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_FOL_GET_REPORT_BY_FOLLOW, Report.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, EmployeeConverter.toModel(follow))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 指定した従業員がフォローしている従業員の日報を、指定されたページ数分取得し、一覧画面に表示する
     * @param follow フォローした従業員
     * @return データの件数
     */
    public long countReport(EmployeeView follow) {
        long reports_count = (long) em.createNamedQuery(JpaConst.Q_FOL_COUNT_REPORT_BY_FOLLOW, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, EmployeeConverter.toModel(follow))
                .getSingleResult();
        return reports_count;
    }

    /**
     * Followsテーブルにデータを登録する
     * @param fv フォローデータ
     */
    public void create(FollowView fv) {

        LocalDateTime ldt = LocalDateTime.now();
        fv.setCreatedAt(ldt);
        fv.setUpdatedAt(ldt);

        //登録処理
        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();
    }

    /**
     * すでにフォローしたかどうか調べる
     * @param follow フォローする従業員
     * @param follower フォローされる従業員
     * @return 重複しているか結果を返す(重複していない:true 重複している:false)
     */
    public boolean isDuplicateFollow(EmployeeView follow,EmployeeView follower) {
        boolean duplicatedFollow = true;

        long followCount = (long) em.createNamedQuery(JpaConst.Q_FOL_COUNT_RESISTERED,Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, EmployeeConverter.toModel(follow))
                .setParameter(JpaConst.JPQL_PARM_FOLLOWER, EmployeeConverter.toModel(follower))
                .getSingleResult();

        if(followCount > 0) {
            duplicatedFollow = false;
        }

        return duplicatedFollow;
    }

}
