package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.LikeConverter;
import actions.views.LikeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Like;

/**
 * いいねテーブルの操作に関わる処理を行うクラス
 */
public class LikeService extends ServiceBase {

    /**
     * 指定した日報idの日報にいいねした従業員を、指定されたページ数分取得し、一覧画面に表示する
     * @param report
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト LikeView
     */
    public List<LikeView> getReportLikes(ReportView report,int page) {

        List<Like> likes = em.createNamedQuery(JpaConst.Q_LIKE_GET_ALL, Like.class)
                .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(report))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return LikeConverter.toViewList(likes);
    }

    /**
     * いいねデータを作成する
     * @param lv いいねデータ
     */
    public void create(LikeView lv) {

        LocalDateTime ldt = LocalDateTime.now();
        lv.setCreatedAt(ldt);
        lv.setUpdatedAt(ldt);

        //登録処理
        em.getTransaction().begin();
        em.persist(LikeConverter.toModel(lv));
        em.getTransaction().commit();
    }

    /**
     * すでにいいねしたかどうか調べる
     * @param employee 従業員データ
     * @param report 日報データ
     * @return 重複しているか結果を返す(重複していない:true 重複している:false)
     */
     public boolean isDuplicateLike(EmployeeView employee,ReportView report) {
         boolean duplicatedLike = true;

         long likeCount = (long) em.createNamedQuery(JpaConst.Q_LIKE_COUNT_ALL_MINE,Long.class)
                 .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                 .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(report))
                 .getSingleResult();

         if(likeCount > 0) {
             duplicatedLike = false;
         }

         return duplicatedLike;
     }
}
