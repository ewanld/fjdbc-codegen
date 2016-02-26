package com.viewbill.bo;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.sql.*;
import com.github.stream4j.Consumer;
import com.github.stream4j.Stream;
import fjdbc.codegen.DaoUtil;
import fjdbc.codegen.DaoUtil.*;
import fjdbc.codegen.Condition;
import fjdbc.codegen.SqlFragment;
import fjdbc.codegen.SqlExpr;
import com.viewbill.bo.Dto.*;

public class Daos {
	
	private Daos() {
		// prevent instanciation
	}
	public static class TMP_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal n = new FieldBigDecimal("N");
		public final FieldString s = new FieldString("S");
		public final FieldTimestamp t = new FieldTimestamp("T");
		
		public TMP_Dao(Connection cnx) {
			super(cnx, "TMP");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<TMP> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from TMP");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal N                    = rs.getBigDecimal("N");
					final String     S                    = rs.getString    ("S");
					final java.sql.Timestamp T                    = rs.getTimestamp ("T");
					final TMP obj = new TMP(N, S, T);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<TMP> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<TMP> res = new ArrayList<TMP>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update TMP set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "TMP", condition);
			return res;
		}

		public int merge(TMP _value) {
			final String sql =
				  " merge into TMP using dual on (N = ? and S = ? and T = ?)"
				+ " when not matched then insert (N, S, T) values (?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.n);
				st.setString    (  2, _value.s);
				st.setTimestamp (  3, _value.t);
				st.setBigDecimal(  4, _value.n);
				st.setString    (  5, _value.s);
				st.setTimestamp (  6, _value.t);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(TMP _value) {
			PreparedStatement st = null;
			final String sql = "insert into TMP(N, S, T) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.n);
				st.setString    (  2, _value.s);
				st.setTimestamp (  3, _value.t);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _n
				, SqlExpr<String> _s
				, SqlExpr<java.sql.Timestamp> _t
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into TMP(N, S, T) values(");
			sql.append(_n.toSql());
			sql.append(", ").append(_s.toSql());
			sql.append(", ").append(_t.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_n.bind(st, parameterIndex);
				_s.bind(st, parameterIndex);
				_t.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<TMP> _values) {
			PreparedStatement st = null;
			final String sql = "insert into TMP(N, S, T) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (TMP _value : _values) {
					st.setBigDecimal(  1, _value.n);
					st.setString    (  2, _value.s);
					st.setTimestamp (  3, _value.t);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_CFDITT_Dao extends Dao {
		private Connection cnx;
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString cfd_codaplemi = new FieldString("CFD_CODAPLEMI");
		public final FieldString cfd_idtope = new FieldString("CFD_IDTOPE");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		public final FieldString cfd_libgrp = new FieldString("CFD_LIBGRP");
		public final FieldString cfd_codac = new FieldString("CFD_CODAC");
		public final FieldString cat_idtcat = new FieldString("CAT_IDTCAT");
		
		public WFA_CFDITT_Dao(Connection cnx) {
			super(cnx, "WFA_CFDITT");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_CFDITT> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_CFDITT");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     CFD_CODAPLEMI        = rs.getString    ("CFD_CODAPLEMI");
					final String     CFD_IDTOPE           = rs.getString    ("CFD_IDTOPE");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final String     CFD_LIBGRP           = rs.getString    ("CFD_LIBGRP");
					final String     CFD_CODAC            = rs.getString    ("CFD_CODAC");
					final String     CAT_IDTCAT           = rs.getString    ("CAT_IDTCAT");
					final WFA_CFDITT obj = new WFA_CFDITT(CPT_NUMCPTFAC, CFD_CODAPLEMI, CFD_IDTOPE, CUS_NUMCUS, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_CFDITT> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_CFDITT> res = new ArrayList<WFA_CFDITT>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_CFDITT set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_CFDITT", condition);
			return res;
		}

		public int merge(WFA_CFDITT _value) {
			final String sql =
				  " merge into WFA_CFDITT using dual on (CPT_NUMCPTFAC = ? and CFD_CODAPLEMI = ?)"
				+ " when matched then update set CFD_IDTOPE = ?, CUS_NUMCUS = ?, CFD_LIBGRP = ?, CFD_CODAC = ?, CAT_IDTCAT = ?"
				+ " when not matched then insert (CPT_NUMCPTFAC, CFD_CODAPLEMI, CFD_IDTOPE, CUS_NUMCUS, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT) values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.cfd_codaplemi);
				st.setString    (  3, _value.cfd_idtope);
				st.setString    (  4, _value.cus_numcus);
				st.setString    (  5, _value.cfd_libgrp);
				st.setString    (  6, _value.cfd_codac);
				st.setString    (  7, _value.cat_idtcat);
				st.setString    (  8, _value.cpt_numcptfac);
				st.setString    (  9, _value.cfd_codaplemi);
				st.setString    ( 10, _value.cfd_idtope);
				st.setString    ( 11, _value.cus_numcus);
				st.setString    ( 12, _value.cfd_libgrp);
				st.setString    ( 13, _value.cfd_codac);
				st.setString    ( 14, _value.cat_idtcat);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_CFDITT _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CFDITT(CPT_NUMCPTFAC, CFD_CODAPLEMI, CFD_IDTOPE, CUS_NUMCUS, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.cfd_codaplemi);
				st.setString    (  3, _value.cfd_idtope);
				st.setString    (  4, _value.cus_numcus);
				st.setString    (  5, _value.cfd_libgrp);
				st.setString    (  6, _value.cfd_codac);
				st.setString    (  7, _value.cat_idtcat);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _cpt_numcptfac
				, SqlExpr<String> _cfd_codaplemi
				, SqlExpr<String> _cfd_idtope
				, SqlExpr<String> _cus_numcus
				, SqlExpr<String> _cfd_libgrp
				, SqlExpr<String> _cfd_codac
				, SqlExpr<String> _cat_idtcat
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_CFDITT(CPT_NUMCPTFAC, CFD_CODAPLEMI, CFD_IDTOPE, CUS_NUMCUS, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT) values(");
			sql.append(_cpt_numcptfac.toSql());
			sql.append(", ").append(_cfd_codaplemi.toSql());
			sql.append(", ").append(_cfd_idtope.toSql());
			sql.append(", ").append(_cus_numcus.toSql());
			sql.append(", ").append(_cfd_libgrp.toSql());
			sql.append(", ").append(_cfd_codac.toSql());
			sql.append(", ").append(_cat_idtcat.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_cpt_numcptfac.bind(st, parameterIndex);
				_cfd_codaplemi.bind(st, parameterIndex);
				_cfd_idtope.bind(st, parameterIndex);
				_cus_numcus.bind(st, parameterIndex);
				_cfd_libgrp.bind(st, parameterIndex);
				_cfd_codac.bind(st, parameterIndex);
				_cat_idtcat.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_CFDITT> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CFDITT(CPT_NUMCPTFAC, CFD_CODAPLEMI, CFD_IDTOPE, CUS_NUMCUS, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_CFDITT _value : _values) {
					st.setString    (  1, _value.cpt_numcptfac);
					st.setString    (  2, _value.cfd_codaplemi);
					st.setString    (  3, _value.cfd_idtope);
					st.setString    (  4, _value.cus_numcus);
					st.setString    (  5, _value.cfd_libgrp);
					st.setString    (  6, _value.cfd_codac);
					st.setString    (  7, _value.cat_idtcat);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_CONFIG_Dao extends Dao {
		private Connection cnx;
		public final FieldString property_name = new FieldString("PROPERTY_NAME");
		public final FieldString property_value = new FieldString("PROPERTY_VALUE");
		
		public WFA_CONFIG_Dao(Connection cnx) {
			super(cnx, "WFA_CONFIG");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_CONFIG> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_CONFIG");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     PROPERTY_NAME        = rs.getString    ("PROPERTY_NAME");
					final String     PROPERTY_VALUE       = rs.getString    ("PROPERTY_VALUE");
					final WFA_CONFIG obj = new WFA_CONFIG(PROPERTY_NAME, PROPERTY_VALUE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_CONFIG> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_CONFIG> res = new ArrayList<WFA_CONFIG>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_CONFIG set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_CONFIG", condition);
			return res;
		}

		public int merge(WFA_CONFIG _value) {
			final String sql =
				  " merge into WFA_CONFIG using dual on (PROPERTY_NAME = ?)"
				+ " when matched then update set PROPERTY_VALUE = ?"
				+ " when not matched then insert (PROPERTY_NAME, PROPERTY_VALUE) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.property_name);
				st.setString    (  2, _value.property_value);
				st.setString    (  3, _value.property_name);
				st.setString    (  4, _value.property_value);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_CONFIG _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CONFIG(PROPERTY_NAME, PROPERTY_VALUE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.property_name);
				st.setString    (  2, _value.property_value);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _property_name
				, SqlExpr<String> _property_value
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_CONFIG(PROPERTY_NAME, PROPERTY_VALUE) values(");
			sql.append(_property_name.toSql());
			sql.append(", ").append(_property_value.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_property_name.bind(st, parameterIndex);
				_property_value.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_CONFIG> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CONFIG(PROPERTY_NAME, PROPERTY_VALUE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_CONFIG _value : _values) {
					st.setString    (  1, _value.property_name);
					st.setString    (  2, _value.property_value);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_CTC_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal ctc_id = new FieldBigDecimal("CTC_ID");
		public final FieldString ctc_ext_id = new FieldString("CTC_EXT_ID");
		public final FieldString ctc_first_name = new FieldString("CTC_FIRST_NAME");
		public final FieldString ctc_last_name = new FieldString("CTC_LAST_NAME");
		public final FieldString ctc_email = new FieldString("CTC_EMAIL");
		
		public WFA_CTC_Dao(Connection cnx) {
			super(cnx, "WFA_CTC");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_CTC> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_CTC");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal CTC_ID               = rs.getBigDecimal("CTC_ID");
					final String     CTC_EXT_ID           = rs.getString    ("CTC_EXT_ID");
					final String     CTC_FIRST_NAME       = rs.getString    ("CTC_FIRST_NAME");
					final String     CTC_LAST_NAME        = rs.getString    ("CTC_LAST_NAME");
					final String     CTC_EMAIL            = rs.getString    ("CTC_EMAIL");
					final WFA_CTC obj = new WFA_CTC(CTC_ID, CTC_EXT_ID, CTC_FIRST_NAME, CTC_LAST_NAME, CTC_EMAIL);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_CTC> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_CTC> res = new ArrayList<WFA_CTC>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_CTC set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_CTC", condition);
			return res;
		}

		public int merge(WFA_CTC _value) {
			final String sql =
				  " merge into WFA_CTC using dual on (CTC_ID = ?)"
				+ " when matched then update set CTC_EXT_ID = ?, CTC_FIRST_NAME = ?, CTC_LAST_NAME = ?, CTC_EMAIL = ?"
				+ " when not matched then insert (CTC_ID, CTC_EXT_ID, CTC_FIRST_NAME, CTC_LAST_NAME, CTC_EMAIL) values (?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.ctc_id);
				st.setString    (  2, _value.ctc_ext_id);
				st.setString    (  3, _value.ctc_first_name);
				st.setString    (  4, _value.ctc_last_name);
				st.setString    (  5, _value.ctc_email);
				st.setBigDecimal(  6, _value.ctc_id);
				st.setString    (  7, _value.ctc_ext_id);
				st.setString    (  8, _value.ctc_first_name);
				st.setString    (  9, _value.ctc_last_name);
				st.setString    ( 10, _value.ctc_email);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_CTC _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CTC(CTC_ID, CTC_EXT_ID, CTC_FIRST_NAME, CTC_LAST_NAME, CTC_EMAIL) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.ctc_id);
				st.setString    (  2, _value.ctc_ext_id);
				st.setString    (  3, _value.ctc_first_name);
				st.setString    (  4, _value.ctc_last_name);
				st.setString    (  5, _value.ctc_email);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _ctc_id
				, SqlExpr<String> _ctc_ext_id
				, SqlExpr<String> _ctc_first_name
				, SqlExpr<String> _ctc_last_name
				, SqlExpr<String> _ctc_email
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_CTC(CTC_ID, CTC_EXT_ID, CTC_FIRST_NAME, CTC_LAST_NAME, CTC_EMAIL) values(");
			sql.append(_ctc_id.toSql());
			sql.append(", ").append(_ctc_ext_id.toSql());
			sql.append(", ").append(_ctc_first_name.toSql());
			sql.append(", ").append(_ctc_last_name.toSql());
			sql.append(", ").append(_ctc_email.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_ctc_id.bind(st, parameterIndex);
				_ctc_ext_id.bind(st, parameterIndex);
				_ctc_first_name.bind(st, parameterIndex);
				_ctc_last_name.bind(st, parameterIndex);
				_ctc_email.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_CTC> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CTC(CTC_ID, CTC_EXT_ID, CTC_FIRST_NAME, CTC_LAST_NAME, CTC_EMAIL) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_CTC _value : _values) {
					st.setBigDecimal(  1, _value.ctc_id);
					st.setString    (  2, _value.ctc_ext_id);
					st.setString    (  3, _value.ctc_first_name);
					st.setString    (  4, _value.ctc_last_name);
					st.setString    (  5, _value.ctc_email);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_CUSFIL_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal file_oid = new FieldBigDecimal("FILE_OID");
		public final FieldString file_name = new FieldString("FILE_NAME");
		public final FieldBigDecimal file_size = new FieldBigDecimal("FILE_SIZE");
		public final FieldString description = new FieldString("DESCRIPTION");
		public final FieldString creation_date = new FieldString("CREATION_DATE");
		public final FieldTimestamp expiry_date = new FieldTimestamp("EXPIRY_DATE");
		
		public WFA_CUSFIL_Dao(Connection cnx) {
			super(cnx, "WFA_CUSFIL");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_CUSFIL> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_CUSFIL");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal FILE_OID             = rs.getBigDecimal("FILE_OID");
					final String     FILE_NAME            = rs.getString    ("FILE_NAME");
					final java.math.BigDecimal FILE_SIZE            = rs.getBigDecimal("FILE_SIZE");
					final String     DESCRIPTION          = rs.getString    ("DESCRIPTION");
					final Object     CREATION_DATE        = rs.getObject    ("CREATION_DATE");
					final java.sql.Timestamp EXPIRY_DATE          = rs.getTimestamp ("EXPIRY_DATE");
					final WFA_CUSFIL obj = new WFA_CUSFIL(FILE_OID, FILE_NAME, FILE_SIZE, DESCRIPTION, CREATION_DATE, EXPIRY_DATE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_CUSFIL> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_CUSFIL> res = new ArrayList<WFA_CUSFIL>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_CUSFIL set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_CUSFIL", condition);
			return res;
		}

		public int merge(WFA_CUSFIL _value) {
			final String sql =
				  " merge into WFA_CUSFIL using dual on (FILE_OID = ?)"
				+ " when matched then update set FILE_NAME = ?, FILE_SIZE = ?, DESCRIPTION = ?, CREATION_DATE = ?, EXPIRY_DATE = ?"
				+ " when not matched then insert (FILE_OID, FILE_NAME, FILE_SIZE, DESCRIPTION, CREATION_DATE, EXPIRY_DATE) values (?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.file_oid);
				st.setString    (  2, _value.file_name);
				st.setBigDecimal(  3, _value.file_size);
				st.setString    (  4, _value.description);
				st.setObject    (  5, _value.creation_date);
				st.setTimestamp (  6, _value.expiry_date);
				st.setBigDecimal(  7, _value.file_oid);
				st.setString    (  8, _value.file_name);
				st.setBigDecimal(  9, _value.file_size);
				st.setString    ( 10, _value.description);
				st.setObject    ( 11, _value.creation_date);
				st.setTimestamp ( 12, _value.expiry_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_CUSFIL _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CUSFIL(FILE_OID, FILE_NAME, FILE_SIZE, DESCRIPTION, CREATION_DATE, EXPIRY_DATE) values(?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.file_oid);
				st.setString    (  2, _value.file_name);
				st.setBigDecimal(  3, _value.file_size);
				st.setString    (  4, _value.description);
				st.setObject    (  5, _value.creation_date);
				st.setTimestamp (  6, _value.expiry_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _file_oid
				, SqlExpr<String> _file_name
				, SqlExpr<java.math.BigDecimal> _file_size
				, SqlExpr<String> _description
				, SqlExpr<Object> _creation_date
				, SqlExpr<java.sql.Timestamp> _expiry_date
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_CUSFIL(FILE_OID, FILE_NAME, FILE_SIZE, DESCRIPTION, CREATION_DATE, EXPIRY_DATE) values(");
			sql.append(_file_oid.toSql());
			sql.append(", ").append(_file_name.toSql());
			sql.append(", ").append(_file_size.toSql());
			sql.append(", ").append(_description.toSql());
			sql.append(", ").append(_creation_date.toSql());
			sql.append(", ").append(_expiry_date.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_file_oid.bind(st, parameterIndex);
				_file_name.bind(st, parameterIndex);
				_file_size.bind(st, parameterIndex);
				_description.bind(st, parameterIndex);
				_creation_date.bind(st, parameterIndex);
				_expiry_date.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_CUSFIL> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_CUSFIL(FILE_OID, FILE_NAME, FILE_SIZE, DESCRIPTION, CREATION_DATE, EXPIRY_DATE) values(?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_CUSFIL _value : _values) {
					st.setBigDecimal(  1, _value.file_oid);
					st.setString    (  2, _value.file_name);
					st.setBigDecimal(  3, _value.file_size);
					st.setString    (  4, _value.description);
					st.setObject    (  5, _value.creation_date);
					st.setTimestamp (  6, _value.expiry_date);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_BAN_ACTION_TRACKING_Dao extends Dao {
		private Connection cnx;
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString last_action = new FieldString("LAST_ACTION");
		public final FieldTimestamp action_date = new FieldTimestamp("ACTION_DATE");
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		
		public WFA_EI_BAN_ACTION_TRACKING_Dao(Connection cnx) {
			super(cnx, "WFA_EI_BAN_ACTION_TRACKING");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_BAN_ACTION_TRACKING> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_BAN_ACTION_TRACKING");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     LAST_ACTION          = rs.getString    ("LAST_ACTION");
					final java.sql.Timestamp ACTION_DATE          = rs.getTimestamp ("ACTION_DATE");
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final WFA_EI_BAN_ACTION_TRACKING obj = new WFA_EI_BAN_ACTION_TRACKING(CPT_NUMCPTFAC, EMAIL, LAST_ACTION, ACTION_DATE, UTL_CODUTL);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_BAN_ACTION_TRACKING> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_BAN_ACTION_TRACKING> res = new ArrayList<WFA_EI_BAN_ACTION_TRACKING>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_BAN_ACTION_TRACKING set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_BAN_ACTION_TRACKING", condition);
			return res;
		}

		public int merge(WFA_EI_BAN_ACTION_TRACKING _value) {
			final String sql =
				  " merge into WFA_EI_BAN_ACTION_TRACKING using dual on (CPT_NUMCPTFAC = ? and EMAIL = ?)"
				+ " when matched then update set LAST_ACTION = ?, ACTION_DATE = ?, UTL_CODUTL = ?"
				+ " when not matched then insert (CPT_NUMCPTFAC, EMAIL, LAST_ACTION, ACTION_DATE, UTL_CODUTL) values (?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.email);
				st.setString    (  3, _value.last_action);
				st.setTimestamp (  4, _value.action_date);
				st.setString    (  5, _value.utl_codutl);
				st.setString    (  6, _value.cpt_numcptfac);
				st.setString    (  7, _value.email);
				st.setString    (  8, _value.last_action);
				st.setTimestamp (  9, _value.action_date);
				st.setString    ( 10, _value.utl_codutl);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_BAN_ACTION_TRACKING _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_ACTION_TRACKING(CPT_NUMCPTFAC, EMAIL, LAST_ACTION, ACTION_DATE, UTL_CODUTL) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.email);
				st.setString    (  3, _value.last_action);
				st.setTimestamp (  4, _value.action_date);
				st.setString    (  5, _value.utl_codutl);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _cpt_numcptfac
				, SqlExpr<String> _email
				, SqlExpr<String> _last_action
				, SqlExpr<java.sql.Timestamp> _action_date
				, SqlExpr<String> _utl_codutl
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_BAN_ACTION_TRACKING(CPT_NUMCPTFAC, EMAIL, LAST_ACTION, ACTION_DATE, UTL_CODUTL) values(");
			sql.append(_cpt_numcptfac.toSql());
			sql.append(", ").append(_email.toSql());
			sql.append(", ").append(_last_action.toSql());
			sql.append(", ").append(_action_date.toSql());
			sql.append(", ").append(_utl_codutl.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_cpt_numcptfac.bind(st, parameterIndex);
				_email.bind(st, parameterIndex);
				_last_action.bind(st, parameterIndex);
				_action_date.bind(st, parameterIndex);
				_utl_codutl.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_BAN_ACTION_TRACKING> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_ACTION_TRACKING(CPT_NUMCPTFAC, EMAIL, LAST_ACTION, ACTION_DATE, UTL_CODUTL) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_BAN_ACTION_TRACKING _value : _values) {
					st.setString    (  1, _value.cpt_numcptfac);
					st.setString    (  2, _value.email);
					st.setString    (  3, _value.last_action);
					st.setTimestamp (  4, _value.action_date);
					st.setString    (  5, _value.utl_codutl);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_BAN_CORE_Dao extends Dao {
		private Connection cnx;
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldTimestamp modification_date = new FieldTimestamp("MODIFICATION_DATE");
		public final FieldString validation_token = new FieldString("VALIDATION_TOKEN");
		public final FieldString core_notified = new FieldString("CORE_NOTIFIED");
		
		public WFA_EI_BAN_CORE_Dao(Connection cnx) {
			super(cnx, "WFA_EI_BAN_CORE");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_BAN_CORE> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_BAN_CORE");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final java.sql.Timestamp MODIFICATION_DATE    = rs.getTimestamp ("MODIFICATION_DATE");
					final String     VALIDATION_TOKEN     = rs.getString    ("VALIDATION_TOKEN");
					final String     CORE_NOTIFIED        = rs.getString    ("CORE_NOTIFIED");
					final WFA_EI_BAN_CORE obj = new WFA_EI_BAN_CORE(CPT_NUMCPTFAC, EMAIL, MODIFICATION_DATE, VALIDATION_TOKEN, CORE_NOTIFIED);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_BAN_CORE> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_BAN_CORE> res = new ArrayList<WFA_EI_BAN_CORE>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_BAN_CORE set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_BAN_CORE", condition);
			return res;
		}

		public int merge(WFA_EI_BAN_CORE _value) {
			final String sql =
				  " merge into WFA_EI_BAN_CORE using dual on (CPT_NUMCPTFAC = ?)"
				+ " when matched then update set EMAIL = ?, MODIFICATION_DATE = ?, VALIDATION_TOKEN = ?, CORE_NOTIFIED = ?"
				+ " when not matched then insert (CPT_NUMCPTFAC, EMAIL, MODIFICATION_DATE, VALIDATION_TOKEN, CORE_NOTIFIED) values (?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.email);
				st.setTimestamp (  3, _value.modification_date);
				st.setString    (  4, _value.validation_token);
				st.setString    (  5, _value.core_notified);
				st.setString    (  6, _value.cpt_numcptfac);
				st.setString    (  7, _value.email);
				st.setTimestamp (  8, _value.modification_date);
				st.setString    (  9, _value.validation_token);
				st.setString    ( 10, _value.core_notified);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_BAN_CORE _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_CORE(CPT_NUMCPTFAC, EMAIL, MODIFICATION_DATE, VALIDATION_TOKEN, CORE_NOTIFIED) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.email);
				st.setTimestamp (  3, _value.modification_date);
				st.setString    (  4, _value.validation_token);
				st.setString    (  5, _value.core_notified);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _cpt_numcptfac
				, SqlExpr<String> _email
				, SqlExpr<java.sql.Timestamp> _modification_date
				, SqlExpr<String> _validation_token
				, SqlExpr<String> _core_notified
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_BAN_CORE(CPT_NUMCPTFAC, EMAIL, MODIFICATION_DATE, VALIDATION_TOKEN, CORE_NOTIFIED) values(");
			sql.append(_cpt_numcptfac.toSql());
			sql.append(", ").append(_email.toSql());
			sql.append(", ").append(_modification_date.toSql());
			sql.append(", ").append(_validation_token.toSql());
			sql.append(", ").append(_core_notified.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_cpt_numcptfac.bind(st, parameterIndex);
				_email.bind(st, parameterIndex);
				_modification_date.bind(st, parameterIndex);
				_validation_token.bind(st, parameterIndex);
				_core_notified.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_BAN_CORE> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_CORE(CPT_NUMCPTFAC, EMAIL, MODIFICATION_DATE, VALIDATION_TOKEN, CORE_NOTIFIED) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_BAN_CORE _value : _values) {
					st.setString    (  1, _value.cpt_numcptfac);
					st.setString    (  2, _value.email);
					st.setTimestamp (  3, _value.modification_date);
					st.setString    (  4, _value.validation_token);
					st.setString    (  5, _value.core_notified);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_BAN_VIEWBILL_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal subscription_id = new FieldBigDecimal("SUBSCRIPTION_ID");
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		
		public WFA_EI_BAN_VIEWBILL_Dao(Connection cnx) {
			super(cnx, "WFA_EI_BAN_VIEWBILL");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_BAN_VIEWBILL> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_BAN_VIEWBILL");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal SUBSCRIPTION_ID      = rs.getBigDecimal("SUBSCRIPTION_ID");
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final WFA_EI_BAN_VIEWBILL obj = new WFA_EI_BAN_VIEWBILL(SUBSCRIPTION_ID, CPT_NUMCPTFAC);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_BAN_VIEWBILL> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_BAN_VIEWBILL> res = new ArrayList<WFA_EI_BAN_VIEWBILL>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_BAN_VIEWBILL set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_BAN_VIEWBILL", condition);
			return res;
		}

		public int merge(WFA_EI_BAN_VIEWBILL _value) {
			final String sql =
				  " merge into WFA_EI_BAN_VIEWBILL using dual on (SUBSCRIPTION_ID = ? and CPT_NUMCPTFAC = ?)"
				+ " when not matched then insert (SUBSCRIPTION_ID, CPT_NUMCPTFAC) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.subscription_id);
				st.setString    (  2, _value.cpt_numcptfac);
				st.setBigDecimal(  3, _value.subscription_id);
				st.setString    (  4, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_BAN_VIEWBILL _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_VIEWBILL(SUBSCRIPTION_ID, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.subscription_id);
				st.setString    (  2, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _subscription_id
				, SqlExpr<String> _cpt_numcptfac
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_BAN_VIEWBILL(SUBSCRIPTION_ID, CPT_NUMCPTFAC) values(");
			sql.append(_subscription_id.toSql());
			sql.append(", ").append(_cpt_numcptfac.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_subscription_id.bind(st, parameterIndex);
				_cpt_numcptfac.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_BAN_VIEWBILL> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_BAN_VIEWBILL(SUBSCRIPTION_ID, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_BAN_VIEWBILL _value : _values) {
					st.setBigDecimal(  1, _value.subscription_id);
					st.setString    (  2, _value.cpt_numcptfac);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_FEATURE_Dao extends Dao {
		private Connection cnx;
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString format = new FieldString("FORMAT");
		public final FieldString language = new FieldString("LANGUAGE");
		public final FieldString zip_attachments = new FieldString("ZIP_ATTACHMENTS");
		public final FieldBigDecimal max_size_in_bytes = new FieldBigDecimal("MAX_SIZE_IN_BYTES");
		
		public WFA_EI_FEATURE_Dao(Connection cnx) {
			super(cnx, "WFA_EI_FEATURE");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_FEATURE> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_FEATURE");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     FORMAT               = rs.getString    ("FORMAT");
					final String     LANGUAGE             = rs.getString    ("LANGUAGE");
					final String     ZIP_ATTACHMENTS      = rs.getString    ("ZIP_ATTACHMENTS");
					final java.math.BigDecimal MAX_SIZE_IN_BYTES    = rs.getBigDecimal("MAX_SIZE_IN_BYTES");
					final WFA_EI_FEATURE obj = new WFA_EI_FEATURE(EMAIL, FORMAT, LANGUAGE, ZIP_ATTACHMENTS, MAX_SIZE_IN_BYTES);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_FEATURE> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_FEATURE> res = new ArrayList<WFA_EI_FEATURE>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_FEATURE set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_FEATURE", condition);
			return res;
		}

		public int merge(WFA_EI_FEATURE _value) {
			final String sql =
				  " merge into WFA_EI_FEATURE using dual on (EMAIL = ?)"
				+ " when matched then update set FORMAT = ?, LANGUAGE = ?, ZIP_ATTACHMENTS = ?, MAX_SIZE_IN_BYTES = ?"
				+ " when not matched then insert (EMAIL, FORMAT, LANGUAGE, ZIP_ATTACHMENTS, MAX_SIZE_IN_BYTES) values (?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.email);
				st.setString    (  2, _value.format);
				st.setString    (  3, _value.language);
				st.setString    (  4, _value.zip_attachments);
				st.setBigDecimal(  5, _value.max_size_in_bytes);
				st.setString    (  6, _value.email);
				st.setString    (  7, _value.format);
				st.setString    (  8, _value.language);
				st.setString    (  9, _value.zip_attachments);
				st.setBigDecimal( 10, _value.max_size_in_bytes);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_FEATURE _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_FEATURE(EMAIL, FORMAT, LANGUAGE, ZIP_ATTACHMENTS, MAX_SIZE_IN_BYTES) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.email);
				st.setString    (  2, _value.format);
				st.setString    (  3, _value.language);
				st.setString    (  4, _value.zip_attachments);
				st.setBigDecimal(  5, _value.max_size_in_bytes);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _email
				, SqlExpr<String> _format
				, SqlExpr<String> _language
				, SqlExpr<String> _zip_attachments
				, SqlExpr<java.math.BigDecimal> _max_size_in_bytes
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_FEATURE(EMAIL, FORMAT, LANGUAGE, ZIP_ATTACHMENTS, MAX_SIZE_IN_BYTES) values(");
			sql.append(_email.toSql());
			sql.append(", ").append(_format.toSql());
			sql.append(", ").append(_language.toSql());
			sql.append(", ").append(_zip_attachments.toSql());
			sql.append(", ").append(_max_size_in_bytes.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_email.bind(st, parameterIndex);
				_format.bind(st, parameterIndex);
				_language.bind(st, parameterIndex);
				_zip_attachments.bind(st, parameterIndex);
				_max_size_in_bytes.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_FEATURE> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_FEATURE(EMAIL, FORMAT, LANGUAGE, ZIP_ATTACHMENTS, MAX_SIZE_IN_BYTES) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_FEATURE _value : _values) {
					st.setString    (  1, _value.email);
					st.setString    (  2, _value.format);
					st.setString    (  3, _value.language);
					st.setString    (  4, _value.zip_attachments);
					st.setBigDecimal(  5, _value.max_size_in_bytes);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_MONITORING_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal monitoring_id = new FieldBigDecimal("MONITORING_ID");
		public final FieldString period = new FieldString("PERIOD");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		public final FieldString token = new FieldString("TOKEN");
		
		public WFA_EI_MONITORING_Dao(Connection cnx) {
			super(cnx, "WFA_EI_MONITORING");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_MONITORING> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_MONITORING");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal MONITORING_ID        = rs.getBigDecimal("MONITORING_ID");
					final String     PERIOD               = rs.getString    ("PERIOD");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final String     TOKEN                = rs.getString    ("TOKEN");
					final WFA_EI_MONITORING obj = new WFA_EI_MONITORING(MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_MONITORING> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_MONITORING> res = new ArrayList<WFA_EI_MONITORING>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_MONITORING set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_MONITORING", condition);
			return res;
		}

		public int merge(WFA_EI_MONITORING _value) {
			final String sql =
				  " merge into WFA_EI_MONITORING using dual on (MONITORING_ID = ?)"
				+ " when matched then update set PERIOD = ?, CUS_NUMCUS = ?, TOKEN = ?"
				+ " when not matched then insert (MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN) values (?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setString    (  2, _value.period);
				st.setString    (  3, _value.cus_numcus);
				st.setString    (  4, _value.token);
				st.setBigDecimal(  5, _value.monitoring_id);
				st.setString    (  6, _value.period);
				st.setString    (  7, _value.cus_numcus);
				st.setString    (  8, _value.token);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_MONITORING _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING(MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setString    (  2, _value.period);
				st.setString    (  3, _value.cus_numcus);
				st.setString    (  4, _value.token);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _monitoring_id
				, SqlExpr<String> _period
				, SqlExpr<String> _cus_numcus
				, SqlExpr<String> _token
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_MONITORING(MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN) values(");
			sql.append(_monitoring_id.toSql());
			sql.append(", ").append(_period.toSql());
			sql.append(", ").append(_cus_numcus.toSql());
			sql.append(", ").append(_token.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_monitoring_id.bind(st, parameterIndex);
				_period.bind(st, parameterIndex);
				_cus_numcus.bind(st, parameterIndex);
				_token.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_MONITORING> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING(MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_MONITORING _value : _values) {
					st.setBigDecimal(  1, _value.monitoring_id);
					st.setString    (  2, _value.period);
					st.setString    (  3, _value.cus_numcus);
					st.setString    (  4, _value.token);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_MONITORING_FILE_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal monitoring_id = new FieldBigDecimal("MONITORING_ID");
		public final FieldString folder_path = new FieldString("FOLDER_PATH");
		public final FieldString file_name = new FieldString("FILE_NAME");
		public final FieldBigDecimal file_size_in_bytes = new FieldBigDecimal("FILE_SIZE_IN_BYTES");
		
		public WFA_EI_MONITORING_FILE_Dao(Connection cnx) {
			super(cnx, "WFA_EI_MONITORING_FILE");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_MONITORING_FILE> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_MONITORING_FILE");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal MONITORING_ID        = rs.getBigDecimal("MONITORING_ID");
					final String     FOLDER_PATH          = rs.getString    ("FOLDER_PATH");
					final String     FILE_NAME            = rs.getString    ("FILE_NAME");
					final java.math.BigDecimal FILE_SIZE_IN_BYTES   = rs.getBigDecimal("FILE_SIZE_IN_BYTES");
					final WFA_EI_MONITORING_FILE obj = new WFA_EI_MONITORING_FILE(MONITORING_ID, FOLDER_PATH, FILE_NAME, FILE_SIZE_IN_BYTES);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_MONITORING_FILE> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_MONITORING_FILE> res = new ArrayList<WFA_EI_MONITORING_FILE>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_MONITORING_FILE set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_MONITORING_FILE", condition);
			return res;
		}

		public int merge(WFA_EI_MONITORING_FILE _value) {
			final String sql =
				  " merge into WFA_EI_MONITORING_FILE using dual on (MONITORING_ID = ? and FOLDER_PATH = ? and FILE_NAME = ?)"
				+ " when matched then update set FILE_SIZE_IN_BYTES = ?"
				+ " when not matched then insert (MONITORING_ID, FOLDER_PATH, FILE_NAME, FILE_SIZE_IN_BYTES) values (?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setString    (  2, _value.folder_path);
				st.setString    (  3, _value.file_name);
				st.setBigDecimal(  4, _value.file_size_in_bytes);
				st.setBigDecimal(  5, _value.monitoring_id);
				st.setString    (  6, _value.folder_path);
				st.setString    (  7, _value.file_name);
				st.setBigDecimal(  8, _value.file_size_in_bytes);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_MONITORING_FILE _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING_FILE(MONITORING_ID, FOLDER_PATH, FILE_NAME, FILE_SIZE_IN_BYTES) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setString    (  2, _value.folder_path);
				st.setString    (  3, _value.file_name);
				st.setBigDecimal(  4, _value.file_size_in_bytes);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _monitoring_id
				, SqlExpr<String> _folder_path
				, SqlExpr<String> _file_name
				, SqlExpr<java.math.BigDecimal> _file_size_in_bytes
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_MONITORING_FILE(MONITORING_ID, FOLDER_PATH, FILE_NAME, FILE_SIZE_IN_BYTES) values(");
			sql.append(_monitoring_id.toSql());
			sql.append(", ").append(_folder_path.toSql());
			sql.append(", ").append(_file_name.toSql());
			sql.append(", ").append(_file_size_in_bytes.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_monitoring_id.bind(st, parameterIndex);
				_folder_path.bind(st, parameterIndex);
				_file_name.bind(st, parameterIndex);
				_file_size_in_bytes.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_MONITORING_FILE> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING_FILE(MONITORING_ID, FOLDER_PATH, FILE_NAME, FILE_SIZE_IN_BYTES) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_MONITORING_FILE _value : _values) {
					st.setBigDecimal(  1, _value.monitoring_id);
					st.setString    (  2, _value.folder_path);
					st.setString    (  3, _value.file_name);
					st.setBigDecimal(  4, _value.file_size_in_bytes);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_MONITORING_HISTORY_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal monitoring_id = new FieldBigDecimal("MONITORING_ID");
		public final FieldTimestamp action_date = new FieldTimestamp("ACTION_DATE");
		public final FieldString email_address = new FieldString("EMAIL_ADDRESS");
		public final FieldString email_status = new FieldString("EMAIL_STATUS");
		public final FieldBigDecimal is_core_address = new FieldBigDecimal("IS_CORE_ADDRESS");
		public final FieldString author = new FieldString("AUTHOR");
		public final FieldString user_comment = new FieldString("USER_COMMENT");
		
		public WFA_EI_MONITORING_HISTORY_Dao(Connection cnx) {
			super(cnx, "WFA_EI_MONITORING_HISTORY");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_MONITORING_HISTORY> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_MONITORING_HISTORY");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal MONITORING_ID        = rs.getBigDecimal("MONITORING_ID");
					final java.sql.Timestamp ACTION_DATE          = rs.getTimestamp ("ACTION_DATE");
					final String     EMAIL_ADDRESS        = rs.getString    ("EMAIL_ADDRESS");
					final String     EMAIL_STATUS         = rs.getString    ("EMAIL_STATUS");
					final java.math.BigDecimal IS_CORE_ADDRESS      = rs.getBigDecimal("IS_CORE_ADDRESS");
					final String     AUTHOR               = rs.getString    ("AUTHOR");
					final String     USER_COMMENT         = rs.getString    ("USER_COMMENT");
					final WFA_EI_MONITORING_HISTORY obj = new WFA_EI_MONITORING_HISTORY(MONITORING_ID, ACTION_DATE, EMAIL_ADDRESS, EMAIL_STATUS, IS_CORE_ADDRESS, AUTHOR, USER_COMMENT);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_MONITORING_HISTORY> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_MONITORING_HISTORY> res = new ArrayList<WFA_EI_MONITORING_HISTORY>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_MONITORING_HISTORY set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_MONITORING_HISTORY", condition);
			return res;
		}

		public int merge(WFA_EI_MONITORING_HISTORY _value) {
			final String sql =
				  " merge into WFA_EI_MONITORING_HISTORY using dual on (MONITORING_ID = ? and ACTION_DATE = ?)"
				+ " when matched then update set EMAIL_ADDRESS = ?, EMAIL_STATUS = ?, IS_CORE_ADDRESS = ?, AUTHOR = ?, USER_COMMENT = ?"
				+ " when not matched then insert (MONITORING_ID, ACTION_DATE, EMAIL_ADDRESS, EMAIL_STATUS, IS_CORE_ADDRESS, AUTHOR, USER_COMMENT) values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setTimestamp (  2, _value.action_date);
				st.setString    (  3, _value.email_address);
				st.setString    (  4, _value.email_status);
				st.setBigDecimal(  5, _value.is_core_address);
				st.setString    (  6, _value.author);
				st.setString    (  7, _value.user_comment);
				st.setBigDecimal(  8, _value.monitoring_id);
				st.setTimestamp (  9, _value.action_date);
				st.setString    ( 10, _value.email_address);
				st.setString    ( 11, _value.email_status);
				st.setBigDecimal( 12, _value.is_core_address);
				st.setString    ( 13, _value.author);
				st.setString    ( 14, _value.user_comment);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_MONITORING_HISTORY _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING_HISTORY(MONITORING_ID, ACTION_DATE, EMAIL_ADDRESS, EMAIL_STATUS, IS_CORE_ADDRESS, AUTHOR, USER_COMMENT) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setTimestamp (  2, _value.action_date);
				st.setString    (  3, _value.email_address);
				st.setString    (  4, _value.email_status);
				st.setBigDecimal(  5, _value.is_core_address);
				st.setString    (  6, _value.author);
				st.setString    (  7, _value.user_comment);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _monitoring_id
				, SqlExpr<java.sql.Timestamp> _action_date
				, SqlExpr<String> _email_address
				, SqlExpr<String> _email_status
				, SqlExpr<java.math.BigDecimal> _is_core_address
				, SqlExpr<String> _author
				, SqlExpr<String> _user_comment
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_MONITORING_HISTORY(MONITORING_ID, ACTION_DATE, EMAIL_ADDRESS, EMAIL_STATUS, IS_CORE_ADDRESS, AUTHOR, USER_COMMENT) values(");
			sql.append(_monitoring_id.toSql());
			sql.append(", ").append(_action_date.toSql());
			sql.append(", ").append(_email_address.toSql());
			sql.append(", ").append(_email_status.toSql());
			sql.append(", ").append(_is_core_address.toSql());
			sql.append(", ").append(_author.toSql());
			sql.append(", ").append(_user_comment.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_monitoring_id.bind(st, parameterIndex);
				_action_date.bind(st, parameterIndex);
				_email_address.bind(st, parameterIndex);
				_email_status.bind(st, parameterIndex);
				_is_core_address.bind(st, parameterIndex);
				_author.bind(st, parameterIndex);
				_user_comment.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_MONITORING_HISTORY> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_MONITORING_HISTORY(MONITORING_ID, ACTION_DATE, EMAIL_ADDRESS, EMAIL_STATUS, IS_CORE_ADDRESS, AUTHOR, USER_COMMENT) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_MONITORING_HISTORY _value : _values) {
					st.setBigDecimal(  1, _value.monitoring_id);
					st.setTimestamp (  2, _value.action_date);
					st.setString    (  3, _value.email_address);
					st.setString    (  4, _value.email_status);
					st.setBigDecimal(  5, _value.is_core_address);
					st.setString    (  6, _value.author);
					st.setString    (  7, _value.user_comment);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_REJ_INVOICE_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal monitoring_id = new FieldBigDecimal("MONITORING_ID");
		public final FieldTimestamp insert_date = new FieldTimestamp("INSERT_DATE");
		
		public WFA_EI_REJ_INVOICE_Dao(Connection cnx) {
			super(cnx, "WFA_EI_REJ_INVOICE");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_REJ_INVOICE> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_REJ_INVOICE");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal MONITORING_ID        = rs.getBigDecimal("MONITORING_ID");
					final java.sql.Timestamp INSERT_DATE          = rs.getTimestamp ("INSERT_DATE");
					final WFA_EI_REJ_INVOICE obj = new WFA_EI_REJ_INVOICE(MONITORING_ID, INSERT_DATE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_REJ_INVOICE> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_REJ_INVOICE> res = new ArrayList<WFA_EI_REJ_INVOICE>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_REJ_INVOICE set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_REJ_INVOICE", condition);
			return res;
		}

		public int merge(WFA_EI_REJ_INVOICE _value) {
			final String sql =
				  " merge into WFA_EI_REJ_INVOICE using dual on (MONITORING_ID = ? and INSERT_DATE = ?)"
				+ " when not matched then insert (MONITORING_ID, INSERT_DATE) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setTimestamp (  2, _value.insert_date);
				st.setBigDecimal(  3, _value.monitoring_id);
				st.setTimestamp (  4, _value.insert_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_REJ_INVOICE _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_REJ_INVOICE(MONITORING_ID, INSERT_DATE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.monitoring_id);
				st.setTimestamp (  2, _value.insert_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _monitoring_id
				, SqlExpr<java.sql.Timestamp> _insert_date
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_REJ_INVOICE(MONITORING_ID, INSERT_DATE) values(");
			sql.append(_monitoring_id.toSql());
			sql.append(", ").append(_insert_date.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_monitoring_id.bind(st, parameterIndex);
				_insert_date.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_REJ_INVOICE> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_REJ_INVOICE(MONITORING_ID, INSERT_DATE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_REJ_INVOICE _value : _values) {
					st.setBigDecimal(  1, _value.monitoring_id);
					st.setTimestamp (  2, _value.insert_date);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_REJ_VALIDATION_Dao extends Dao {
		private Connection cnx;
		public final FieldString token = new FieldString("TOKEN");
		public final FieldTimestamp insert_date = new FieldTimestamp("INSERT_DATE");
		
		public WFA_EI_REJ_VALIDATION_Dao(Connection cnx) {
			super(cnx, "WFA_EI_REJ_VALIDATION");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_REJ_VALIDATION> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_REJ_VALIDATION");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     TOKEN                = rs.getString    ("TOKEN");
					final java.sql.Timestamp INSERT_DATE          = rs.getTimestamp ("INSERT_DATE");
					final WFA_EI_REJ_VALIDATION obj = new WFA_EI_REJ_VALIDATION(TOKEN, INSERT_DATE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_REJ_VALIDATION> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_REJ_VALIDATION> res = new ArrayList<WFA_EI_REJ_VALIDATION>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_REJ_VALIDATION set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_REJ_VALIDATION", condition);
			return res;
		}

		public int merge(WFA_EI_REJ_VALIDATION _value) {
			final String sql =
				  " merge into WFA_EI_REJ_VALIDATION using dual on (TOKEN = ? and INSERT_DATE = ?)"
				+ " when not matched then insert (TOKEN, INSERT_DATE) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.token);
				st.setTimestamp (  2, _value.insert_date);
				st.setString    (  3, _value.token);
				st.setTimestamp (  4, _value.insert_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_REJ_VALIDATION _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_REJ_VALIDATION(TOKEN, INSERT_DATE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.token);
				st.setTimestamp (  2, _value.insert_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _token
				, SqlExpr<java.sql.Timestamp> _insert_date
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_REJ_VALIDATION(TOKEN, INSERT_DATE) values(");
			sql.append(_token.toSql());
			sql.append(", ").append(_insert_date.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_token.bind(st, parameterIndex);
				_insert_date.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_REJ_VALIDATION> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_REJ_VALIDATION(TOKEN, INSERT_DATE) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_REJ_VALIDATION _value : _values) {
					st.setString    (  1, _value.token);
					st.setTimestamp (  2, _value.insert_date);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_SUBSCRIPTION_VIEWBILL_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal subscription_id = new FieldBigDecimal("SUBSCRIPTION_ID");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldBigDecimal activation = new FieldBigDecimal("ACTIVATION");
		
		public WFA_EI_SUBSCRIPTION_VIEWBILL_Dao(Connection cnx) {
			super(cnx, "WFA_EI_SUBSCRIPTION_VIEWBILL");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_SUBSCRIPTION_VIEWBILL> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_SUBSCRIPTION_VIEWBILL");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal SUBSCRIPTION_ID      = rs.getBigDecimal("SUBSCRIPTION_ID");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final java.math.BigDecimal ACTIVATION           = rs.getBigDecimal("ACTIVATION");
					final WFA_EI_SUBSCRIPTION_VIEWBILL obj = new WFA_EI_SUBSCRIPTION_VIEWBILL(SUBSCRIPTION_ID, EMAIL, ACTIVATION);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_SUBSCRIPTION_VIEWBILL> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_SUBSCRIPTION_VIEWBILL> res = new ArrayList<WFA_EI_SUBSCRIPTION_VIEWBILL>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_SUBSCRIPTION_VIEWBILL set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_SUBSCRIPTION_VIEWBILL", condition);
			return res;
		}

		public int merge(WFA_EI_SUBSCRIPTION_VIEWBILL _value) {
			final String sql =
				  " merge into WFA_EI_SUBSCRIPTION_VIEWBILL using dual on (SUBSCRIPTION_ID = ?)"
				+ " when matched then update set EMAIL = ?, ACTIVATION = ?"
				+ " when not matched then insert (SUBSCRIPTION_ID, EMAIL, ACTIVATION) values (?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.subscription_id);
				st.setString    (  2, _value.email);
				st.setBigDecimal(  3, _value.activation);
				st.setBigDecimal(  4, _value.subscription_id);
				st.setString    (  5, _value.email);
				st.setBigDecimal(  6, _value.activation);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_SUBSCRIPTION_VIEWBILL _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_SUBSCRIPTION_VIEWBILL(SUBSCRIPTION_ID, EMAIL, ACTIVATION) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.subscription_id);
				st.setString    (  2, _value.email);
				st.setBigDecimal(  3, _value.activation);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _subscription_id
				, SqlExpr<String> _email
				, SqlExpr<java.math.BigDecimal> _activation
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_SUBSCRIPTION_VIEWBILL(SUBSCRIPTION_ID, EMAIL, ACTIVATION) values(");
			sql.append(_subscription_id.toSql());
			sql.append(", ").append(_email.toSql());
			sql.append(", ").append(_activation.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_subscription_id.bind(st, parameterIndex);
				_email.bind(st, parameterIndex);
				_activation.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_SUBSCRIPTION_VIEWBILL> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_SUBSCRIPTION_VIEWBILL(SUBSCRIPTION_ID, EMAIL, ACTIVATION) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_SUBSCRIPTION_VIEWBILL _value : _values) {
					st.setBigDecimal(  1, _value.subscription_id);
					st.setString    (  2, _value.email);
					st.setBigDecimal(  3, _value.activation);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_EI_VALIDATION_EMAIL_Dao extends Dao {
		private Connection cnx;
		public final FieldString token = new FieldString("TOKEN");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString status = new FieldString("STATUS");
		public final FieldTimestamp update_status_date = new FieldTimestamp("UPDATE_STATUS_DATE");
		
		public WFA_EI_VALIDATION_EMAIL_Dao(Connection cnx) {
			super(cnx, "WFA_EI_VALIDATION_EMAIL");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_EI_VALIDATION_EMAIL> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_EI_VALIDATION_EMAIL");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     TOKEN                = rs.getString    ("TOKEN");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     STATUS               = rs.getString    ("STATUS");
					final java.sql.Timestamp UPDATE_STATUS_DATE   = rs.getTimestamp ("UPDATE_STATUS_DATE");
					final WFA_EI_VALIDATION_EMAIL obj = new WFA_EI_VALIDATION_EMAIL(TOKEN, EMAIL, STATUS, UPDATE_STATUS_DATE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_EI_VALIDATION_EMAIL> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_EI_VALIDATION_EMAIL> res = new ArrayList<WFA_EI_VALIDATION_EMAIL>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_EI_VALIDATION_EMAIL set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_EI_VALIDATION_EMAIL", condition);
			return res;
		}

		public int merge(WFA_EI_VALIDATION_EMAIL _value) {
			final String sql =
				  " merge into WFA_EI_VALIDATION_EMAIL using dual on (TOKEN = ?)"
				+ " when matched then update set EMAIL = ?, STATUS = ?, UPDATE_STATUS_DATE = ?"
				+ " when not matched then insert (TOKEN, EMAIL, STATUS, UPDATE_STATUS_DATE) values (?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.token);
				st.setString    (  2, _value.email);
				st.setString    (  3, _value.status);
				st.setTimestamp (  4, _value.update_status_date);
				st.setString    (  5, _value.token);
				st.setString    (  6, _value.email);
				st.setString    (  7, _value.status);
				st.setTimestamp (  8, _value.update_status_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_EI_VALIDATION_EMAIL _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_VALIDATION_EMAIL(TOKEN, EMAIL, STATUS, UPDATE_STATUS_DATE) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.token);
				st.setString    (  2, _value.email);
				st.setString    (  3, _value.status);
				st.setTimestamp (  4, _value.update_status_date);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _token
				, SqlExpr<String> _email
				, SqlExpr<String> _status
				, SqlExpr<java.sql.Timestamp> _update_status_date
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_EI_VALIDATION_EMAIL(TOKEN, EMAIL, STATUS, UPDATE_STATUS_DATE) values(");
			sql.append(_token.toSql());
			sql.append(", ").append(_email.toSql());
			sql.append(", ").append(_status.toSql());
			sql.append(", ").append(_update_status_date.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_token.bind(st, parameterIndex);
				_email.bind(st, parameterIndex);
				_status.bind(st, parameterIndex);
				_update_status_date.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_EI_VALIDATION_EMAIL> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_EI_VALIDATION_EMAIL(TOKEN, EMAIL, STATUS, UPDATE_STATUS_DATE) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_EI_VALIDATION_EMAIL _value : _values) {
					st.setString    (  1, _value.token);
					st.setString    (  2, _value.email);
					st.setString    (  3, _value.status);
					st.setTimestamp (  4, _value.update_status_date);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_FACPDF_Dao extends Dao {
		private Connection cnx;
		public final FieldString fac_numfacree = new FieldString("FAC_NUMFACREE");
		public final FieldBigDecimal fac_numfac = new FieldBigDecimal("FAC_NUMFAC");
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString fac_nomdom = new FieldString("FAC_NOMDOM");
		public final FieldBigDecimal fac_mnttotttc = new FieldBigDecimal("FAC_MNTTOTTTC");
		public final FieldString pdf_nomper = new FieldString("PDF_NOMPER");
		public final FieldString pdf_nomrep = new FieldString("PDF_NOMREP");
		public final FieldString pdf_nomfic = new FieldString("PDF_NOMFIC");
		public final FieldString pdf_pfxtab = new FieldString("PDF_PFXTAB");
		public final FieldString pdf_nomrepcsv = new FieldString("PDF_NOMREPCSV");
		public final FieldString pdf_nomficcsv = new FieldString("PDF_NOMFICCSV");
		public final FieldString pdf_nomrepxml = new FieldString("PDF_NOMREPXML");
		public final FieldString pdf_nomficxml = new FieldString("PDF_NOMFICXML");
		public final FieldString pdf_nomrepf94 = new FieldString("PDF_NOMREPF94");
		public final FieldString pdf_nomficf94 = new FieldString("PDF_NOMFICF94");
		public final FieldString lbf_codfac = new FieldString("LBF_CODFAC");
		public final FieldTimestamp fac_datfac = new FieldTimestamp("FAC_DATFAC");
		public final FieldString fam_codfamfac = new FieldString("FAM_CODFAMFAC");
		public final FieldString lbf_codree = new FieldString("LBF_CODREE");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		public final FieldBigDecimal env_id = new FieldBigDecimal("ENV_ID");
		public final FieldString fac_devfac = new FieldString("FAC_DEVFAC");
		public final FieldBigDecimal fac_mnttotht = new FieldBigDecimal("FAC_MNTTOTHT");
		public final FieldBigDecimal is_available = new FieldBigDecimal("IS_AVAILABLE");
		public final FieldBigDecimal is_out_of_scope = new FieldBigDecimal("IS_OUT_OF_SCOPE");
		public final FieldTimestamp fac_period_from = new FieldTimestamp("FAC_PERIOD_FROM");
		public final FieldTimestamp fac_period_to = new FieldTimestamp("FAC_PERIOD_TO");
		public final FieldString notif = new FieldString("NOTIF");
		
		public WFA_FACPDF_Dao(Connection cnx) {
			super(cnx, "WFA_FACPDF");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_FACPDF> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_FACPDF");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     FAC_NUMFACREE        = rs.getString    ("FAC_NUMFACREE");
					final java.math.BigDecimal FAC_NUMFAC           = rs.getBigDecimal("FAC_NUMFAC");
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     FAC_NOMDOM           = rs.getString    ("FAC_NOMDOM");
					final java.math.BigDecimal FAC_MNTTOTTTC        = rs.getBigDecimal("FAC_MNTTOTTTC");
					final String     PDF_NOMPER           = rs.getString    ("PDF_NOMPER");
					final String     PDF_NOMREP           = rs.getString    ("PDF_NOMREP");
					final String     PDF_NOMFIC           = rs.getString    ("PDF_NOMFIC");
					final String     PDF_PFXTAB           = rs.getString    ("PDF_PFXTAB");
					final String     PDF_NOMREPCSV        = rs.getString    ("PDF_NOMREPCSV");
					final String     PDF_NOMFICCSV        = rs.getString    ("PDF_NOMFICCSV");
					final String     PDF_NOMREPXML        = rs.getString    ("PDF_NOMREPXML");
					final String     PDF_NOMFICXML        = rs.getString    ("PDF_NOMFICXML");
					final String     PDF_NOMREPF94        = rs.getString    ("PDF_NOMREPF94");
					final String     PDF_NOMFICF94        = rs.getString    ("PDF_NOMFICF94");
					final String     LBF_CODFAC           = rs.getString    ("LBF_CODFAC");
					final java.sql.Timestamp FAC_DATFAC           = rs.getTimestamp ("FAC_DATFAC");
					final String     FAM_CODFAMFAC        = rs.getString    ("FAM_CODFAMFAC");
					final String     LBF_CODREE           = rs.getString    ("LBF_CODREE");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final java.math.BigDecimal ENV_ID               = rs.getBigDecimal("ENV_ID");
					final String     FAC_DEVFAC           = rs.getString    ("FAC_DEVFAC");
					final java.math.BigDecimal FAC_MNTTOTHT         = rs.getBigDecimal("FAC_MNTTOTHT");
					final java.math.BigDecimal IS_AVAILABLE         = rs.getBigDecimal("IS_AVAILABLE");
					final java.math.BigDecimal IS_OUT_OF_SCOPE      = rs.getBigDecimal("IS_OUT_OF_SCOPE");
					final java.sql.Timestamp FAC_PERIOD_FROM      = rs.getTimestamp ("FAC_PERIOD_FROM");
					final java.sql.Timestamp FAC_PERIOD_TO        = rs.getTimestamp ("FAC_PERIOD_TO");
					final String     NOTIF                = rs.getString    ("NOTIF");
					final WFA_FACPDF obj = new WFA_FACPDF(FAC_NUMFACREE, FAC_NUMFAC, CPT_NUMCPTFAC, FAC_NOMDOM, FAC_MNTTOTTTC, PDF_NOMPER, PDF_NOMREP, PDF_NOMFIC, PDF_PFXTAB, PDF_NOMREPCSV, PDF_NOMFICCSV, PDF_NOMREPXML, PDF_NOMFICXML, PDF_NOMREPF94, PDF_NOMFICF94, LBF_CODFAC, FAC_DATFAC, FAM_CODFAMFAC, LBF_CODREE, CUS_NUMCUS, ENV_ID, FAC_DEVFAC, FAC_MNTTOTHT, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_PERIOD_FROM, FAC_PERIOD_TO, NOTIF);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_FACPDF> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_FACPDF> res = new ArrayList<WFA_FACPDF>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_FACPDF set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_FACPDF", condition);
			return res;
		}

		public int merge(WFA_FACPDF _value) {
			final String sql =
				  " merge into WFA_FACPDF using dual on (FAC_NUMFACREE = ?)"
				+ " when matched then update set FAC_NUMFAC = ?, CPT_NUMCPTFAC = ?, FAC_NOMDOM = ?, FAC_MNTTOTTTC = ?, PDF_NOMPER = ?, PDF_NOMREP = ?, PDF_NOMFIC = ?, PDF_PFXTAB = ?, PDF_NOMREPCSV = ?, PDF_NOMFICCSV = ?, PDF_NOMREPXML = ?, PDF_NOMFICXML = ?, PDF_NOMREPF94 = ?, PDF_NOMFICF94 = ?, LBF_CODFAC = ?, FAC_DATFAC = ?, FAM_CODFAMFAC = ?, LBF_CODREE = ?, CUS_NUMCUS = ?, ENV_ID = ?, FAC_DEVFAC = ?, FAC_MNTTOTHT = ?, IS_AVAILABLE = ?, IS_OUT_OF_SCOPE = ?, FAC_PERIOD_FROM = ?, FAC_PERIOD_TO = ?, NOTIF = ?"
				+ " when not matched then insert (FAC_NUMFACREE, FAC_NUMFAC, CPT_NUMCPTFAC, FAC_NOMDOM, FAC_MNTTOTTTC, PDF_NOMPER, PDF_NOMREP, PDF_NOMFIC, PDF_PFXTAB, PDF_NOMREPCSV, PDF_NOMFICCSV, PDF_NOMREPXML, PDF_NOMFICXML, PDF_NOMREPF94, PDF_NOMFICF94, LBF_CODFAC, FAC_DATFAC, FAM_CODFAMFAC, LBF_CODREE, CUS_NUMCUS, ENV_ID, FAC_DEVFAC, FAC_MNTTOTHT, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_PERIOD_FROM, FAC_PERIOD_TO, NOTIF) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.fac_numfacree);
				st.setBigDecimal(  2, _value.fac_numfac);
				st.setString    (  3, _value.cpt_numcptfac);
				st.setString    (  4, _value.fac_nomdom);
				st.setBigDecimal(  5, _value.fac_mnttotttc);
				st.setString    (  6, _value.pdf_nomper);
				st.setString    (  7, _value.pdf_nomrep);
				st.setString    (  8, _value.pdf_nomfic);
				st.setString    (  9, _value.pdf_pfxtab);
				st.setString    ( 10, _value.pdf_nomrepcsv);
				st.setString    ( 11, _value.pdf_nomficcsv);
				st.setString    ( 12, _value.pdf_nomrepxml);
				st.setString    ( 13, _value.pdf_nomficxml);
				st.setString    ( 14, _value.pdf_nomrepf94);
				st.setString    ( 15, _value.pdf_nomficf94);
				st.setString    ( 16, _value.lbf_codfac);
				st.setTimestamp ( 17, _value.fac_datfac);
				st.setString    ( 18, _value.fam_codfamfac);
				st.setString    ( 19, _value.lbf_codree);
				st.setString    ( 20, _value.cus_numcus);
				st.setBigDecimal( 21, _value.env_id);
				st.setString    ( 22, _value.fac_devfac);
				st.setBigDecimal( 23, _value.fac_mnttotht);
				st.setBigDecimal( 24, _value.is_available);
				st.setBigDecimal( 25, _value.is_out_of_scope);
				st.setTimestamp ( 26, _value.fac_period_from);
				st.setTimestamp ( 27, _value.fac_period_to);
				st.setString    ( 28, _value.notif);
				st.setString    ( 29, _value.fac_numfacree);
				st.setBigDecimal( 30, _value.fac_numfac);
				st.setString    ( 31, _value.cpt_numcptfac);
				st.setString    ( 32, _value.fac_nomdom);
				st.setBigDecimal( 33, _value.fac_mnttotttc);
				st.setString    ( 34, _value.pdf_nomper);
				st.setString    ( 35, _value.pdf_nomrep);
				st.setString    ( 36, _value.pdf_nomfic);
				st.setString    ( 37, _value.pdf_pfxtab);
				st.setString    ( 38, _value.pdf_nomrepcsv);
				st.setString    ( 39, _value.pdf_nomficcsv);
				st.setString    ( 40, _value.pdf_nomrepxml);
				st.setString    ( 41, _value.pdf_nomficxml);
				st.setString    ( 42, _value.pdf_nomrepf94);
				st.setString    ( 43, _value.pdf_nomficf94);
				st.setString    ( 44, _value.lbf_codfac);
				st.setTimestamp ( 45, _value.fac_datfac);
				st.setString    ( 46, _value.fam_codfamfac);
				st.setString    ( 47, _value.lbf_codree);
				st.setString    ( 48, _value.cus_numcus);
				st.setBigDecimal( 49, _value.env_id);
				st.setString    ( 50, _value.fac_devfac);
				st.setBigDecimal( 51, _value.fac_mnttotht);
				st.setBigDecimal( 52, _value.is_available);
				st.setBigDecimal( 53, _value.is_out_of_scope);
				st.setTimestamp ( 54, _value.fac_period_from);
				st.setTimestamp ( 55, _value.fac_period_to);
				st.setString    ( 56, _value.notif);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_FACPDF _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FACPDF(FAC_NUMFACREE, FAC_NUMFAC, CPT_NUMCPTFAC, FAC_NOMDOM, FAC_MNTTOTTTC, PDF_NOMPER, PDF_NOMREP, PDF_NOMFIC, PDF_PFXTAB, PDF_NOMREPCSV, PDF_NOMFICCSV, PDF_NOMREPXML, PDF_NOMFICXML, PDF_NOMREPF94, PDF_NOMFICF94, LBF_CODFAC, FAC_DATFAC, FAM_CODFAMFAC, LBF_CODREE, CUS_NUMCUS, ENV_ID, FAC_DEVFAC, FAC_MNTTOTHT, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_PERIOD_FROM, FAC_PERIOD_TO, NOTIF) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.fac_numfacree);
				st.setBigDecimal(  2, _value.fac_numfac);
				st.setString    (  3, _value.cpt_numcptfac);
				st.setString    (  4, _value.fac_nomdom);
				st.setBigDecimal(  5, _value.fac_mnttotttc);
				st.setString    (  6, _value.pdf_nomper);
				st.setString    (  7, _value.pdf_nomrep);
				st.setString    (  8, _value.pdf_nomfic);
				st.setString    (  9, _value.pdf_pfxtab);
				st.setString    ( 10, _value.pdf_nomrepcsv);
				st.setString    ( 11, _value.pdf_nomficcsv);
				st.setString    ( 12, _value.pdf_nomrepxml);
				st.setString    ( 13, _value.pdf_nomficxml);
				st.setString    ( 14, _value.pdf_nomrepf94);
				st.setString    ( 15, _value.pdf_nomficf94);
				st.setString    ( 16, _value.lbf_codfac);
				st.setTimestamp ( 17, _value.fac_datfac);
				st.setString    ( 18, _value.fam_codfamfac);
				st.setString    ( 19, _value.lbf_codree);
				st.setString    ( 20, _value.cus_numcus);
				st.setBigDecimal( 21, _value.env_id);
				st.setString    ( 22, _value.fac_devfac);
				st.setBigDecimal( 23, _value.fac_mnttotht);
				st.setBigDecimal( 24, _value.is_available);
				st.setBigDecimal( 25, _value.is_out_of_scope);
				st.setTimestamp ( 26, _value.fac_period_from);
				st.setTimestamp ( 27, _value.fac_period_to);
				st.setString    ( 28, _value.notif);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _fac_numfacree
				, SqlExpr<java.math.BigDecimal> _fac_numfac
				, SqlExpr<String> _cpt_numcptfac
				, SqlExpr<String> _fac_nomdom
				, SqlExpr<java.math.BigDecimal> _fac_mnttotttc
				, SqlExpr<String> _pdf_nomper
				, SqlExpr<String> _pdf_nomrep
				, SqlExpr<String> _pdf_nomfic
				, SqlExpr<String> _pdf_pfxtab
				, SqlExpr<String> _pdf_nomrepcsv
				, SqlExpr<String> _pdf_nomficcsv
				, SqlExpr<String> _pdf_nomrepxml
				, SqlExpr<String> _pdf_nomficxml
				, SqlExpr<String> _pdf_nomrepf94
				, SqlExpr<String> _pdf_nomficf94
				, SqlExpr<String> _lbf_codfac
				, SqlExpr<java.sql.Timestamp> _fac_datfac
				, SqlExpr<String> _fam_codfamfac
				, SqlExpr<String> _lbf_codree
				, SqlExpr<String> _cus_numcus
				, SqlExpr<java.math.BigDecimal> _env_id
				, SqlExpr<String> _fac_devfac
				, SqlExpr<java.math.BigDecimal> _fac_mnttotht
				, SqlExpr<java.math.BigDecimal> _is_available
				, SqlExpr<java.math.BigDecimal> _is_out_of_scope
				, SqlExpr<java.sql.Timestamp> _fac_period_from
				, SqlExpr<java.sql.Timestamp> _fac_period_to
				, SqlExpr<String> _notif
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_FACPDF(FAC_NUMFACREE, FAC_NUMFAC, CPT_NUMCPTFAC, FAC_NOMDOM, FAC_MNTTOTTTC, PDF_NOMPER, PDF_NOMREP, PDF_NOMFIC, PDF_PFXTAB, PDF_NOMREPCSV, PDF_NOMFICCSV, PDF_NOMREPXML, PDF_NOMFICXML, PDF_NOMREPF94, PDF_NOMFICF94, LBF_CODFAC, FAC_DATFAC, FAM_CODFAMFAC, LBF_CODREE, CUS_NUMCUS, ENV_ID, FAC_DEVFAC, FAC_MNTTOTHT, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_PERIOD_FROM, FAC_PERIOD_TO, NOTIF) values(");
			sql.append(_fac_numfacree.toSql());
			sql.append(", ").append(_fac_numfac.toSql());
			sql.append(", ").append(_cpt_numcptfac.toSql());
			sql.append(", ").append(_fac_nomdom.toSql());
			sql.append(", ").append(_fac_mnttotttc.toSql());
			sql.append(", ").append(_pdf_nomper.toSql());
			sql.append(", ").append(_pdf_nomrep.toSql());
			sql.append(", ").append(_pdf_nomfic.toSql());
			sql.append(", ").append(_pdf_pfxtab.toSql());
			sql.append(", ").append(_pdf_nomrepcsv.toSql());
			sql.append(", ").append(_pdf_nomficcsv.toSql());
			sql.append(", ").append(_pdf_nomrepxml.toSql());
			sql.append(", ").append(_pdf_nomficxml.toSql());
			sql.append(", ").append(_pdf_nomrepf94.toSql());
			sql.append(", ").append(_pdf_nomficf94.toSql());
			sql.append(", ").append(_lbf_codfac.toSql());
			sql.append(", ").append(_fac_datfac.toSql());
			sql.append(", ").append(_fam_codfamfac.toSql());
			sql.append(", ").append(_lbf_codree.toSql());
			sql.append(", ").append(_cus_numcus.toSql());
			sql.append(", ").append(_env_id.toSql());
			sql.append(", ").append(_fac_devfac.toSql());
			sql.append(", ").append(_fac_mnttotht.toSql());
			sql.append(", ").append(_is_available.toSql());
			sql.append(", ").append(_is_out_of_scope.toSql());
			sql.append(", ").append(_fac_period_from.toSql());
			sql.append(", ").append(_fac_period_to.toSql());
			sql.append(", ").append(_notif.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_fac_numfacree.bind(st, parameterIndex);
				_fac_numfac.bind(st, parameterIndex);
				_cpt_numcptfac.bind(st, parameterIndex);
				_fac_nomdom.bind(st, parameterIndex);
				_fac_mnttotttc.bind(st, parameterIndex);
				_pdf_nomper.bind(st, parameterIndex);
				_pdf_nomrep.bind(st, parameterIndex);
				_pdf_nomfic.bind(st, parameterIndex);
				_pdf_pfxtab.bind(st, parameterIndex);
				_pdf_nomrepcsv.bind(st, parameterIndex);
				_pdf_nomficcsv.bind(st, parameterIndex);
				_pdf_nomrepxml.bind(st, parameterIndex);
				_pdf_nomficxml.bind(st, parameterIndex);
				_pdf_nomrepf94.bind(st, parameterIndex);
				_pdf_nomficf94.bind(st, parameterIndex);
				_lbf_codfac.bind(st, parameterIndex);
				_fac_datfac.bind(st, parameterIndex);
				_fam_codfamfac.bind(st, parameterIndex);
				_lbf_codree.bind(st, parameterIndex);
				_cus_numcus.bind(st, parameterIndex);
				_env_id.bind(st, parameterIndex);
				_fac_devfac.bind(st, parameterIndex);
				_fac_mnttotht.bind(st, parameterIndex);
				_is_available.bind(st, parameterIndex);
				_is_out_of_scope.bind(st, parameterIndex);
				_fac_period_from.bind(st, parameterIndex);
				_fac_period_to.bind(st, parameterIndex);
				_notif.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_FACPDF> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FACPDF(FAC_NUMFACREE, FAC_NUMFAC, CPT_NUMCPTFAC, FAC_NOMDOM, FAC_MNTTOTTTC, PDF_NOMPER, PDF_NOMREP, PDF_NOMFIC, PDF_PFXTAB, PDF_NOMREPCSV, PDF_NOMFICCSV, PDF_NOMREPXML, PDF_NOMFICXML, PDF_NOMREPF94, PDF_NOMFICF94, LBF_CODFAC, FAC_DATFAC, FAM_CODFAMFAC, LBF_CODREE, CUS_NUMCUS, ENV_ID, FAC_DEVFAC, FAC_MNTTOTHT, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_PERIOD_FROM, FAC_PERIOD_TO, NOTIF) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_FACPDF _value : _values) {
					st.setString    (  1, _value.fac_numfacree);
					st.setBigDecimal(  2, _value.fac_numfac);
					st.setString    (  3, _value.cpt_numcptfac);
					st.setString    (  4, _value.fac_nomdom);
					st.setBigDecimal(  5, _value.fac_mnttotttc);
					st.setString    (  6, _value.pdf_nomper);
					st.setString    (  7, _value.pdf_nomrep);
					st.setString    (  8, _value.pdf_nomfic);
					st.setString    (  9, _value.pdf_pfxtab);
					st.setString    ( 10, _value.pdf_nomrepcsv);
					st.setString    ( 11, _value.pdf_nomficcsv);
					st.setString    ( 12, _value.pdf_nomrepxml);
					st.setString    ( 13, _value.pdf_nomficxml);
					st.setString    ( 14, _value.pdf_nomrepf94);
					st.setString    ( 15, _value.pdf_nomficf94);
					st.setString    ( 16, _value.lbf_codfac);
					st.setTimestamp ( 17, _value.fac_datfac);
					st.setString    ( 18, _value.fam_codfamfac);
					st.setString    ( 19, _value.lbf_codree);
					st.setString    ( 20, _value.cus_numcus);
					st.setBigDecimal( 21, _value.env_id);
					st.setString    ( 22, _value.fac_devfac);
					st.setBigDecimal( 23, _value.fac_mnttotht);
					st.setBigDecimal( 24, _value.is_available);
					st.setBigDecimal( 25, _value.is_out_of_scope);
					st.setTimestamp ( 26, _value.fac_period_from);
					st.setTimestamp ( 27, _value.fac_period_to);
					st.setString    ( 28, _value.notif);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_FILAXS_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal file_oid = new FieldBigDecimal("FILE_OID");
		public final FieldString adee_type = new FieldString("ADEE_TYPE");
		public final FieldString adee_id = new FieldString("ADEE_ID");
		
		public WFA_FILAXS_Dao(Connection cnx) {
			super(cnx, "WFA_FILAXS");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_FILAXS> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_FILAXS");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal FILE_OID             = rs.getBigDecimal("FILE_OID");
					final String     ADEE_TYPE            = rs.getString    ("ADEE_TYPE");
					final String     ADEE_ID              = rs.getString    ("ADEE_ID");
					final WFA_FILAXS obj = new WFA_FILAXS(FILE_OID, ADEE_TYPE, ADEE_ID);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_FILAXS> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_FILAXS> res = new ArrayList<WFA_FILAXS>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_FILAXS set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_FILAXS", condition);
			return res;
		}

		public int merge(WFA_FILAXS _value) {
			final String sql =
				  " merge into WFA_FILAXS using dual on (FILE_OID = ? and ADEE_TYPE = ? and ADEE_ID = ?)"
				+ " when not matched then insert (FILE_OID, ADEE_TYPE, ADEE_ID) values (?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.file_oid);
				st.setString    (  2, _value.adee_type);
				st.setString    (  3, _value.adee_id);
				st.setBigDecimal(  4, _value.file_oid);
				st.setString    (  5, _value.adee_type);
				st.setString    (  6, _value.adee_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_FILAXS _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FILAXS(FILE_OID, ADEE_TYPE, ADEE_ID) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.file_oid);
				st.setString    (  2, _value.adee_type);
				st.setString    (  3, _value.adee_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _file_oid
				, SqlExpr<String> _adee_type
				, SqlExpr<String> _adee_id
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_FILAXS(FILE_OID, ADEE_TYPE, ADEE_ID) values(");
			sql.append(_file_oid.toSql());
			sql.append(", ").append(_adee_type.toSql());
			sql.append(", ").append(_adee_id.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_file_oid.bind(st, parameterIndex);
				_adee_type.bind(st, parameterIndex);
				_adee_id.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_FILAXS> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FILAXS(FILE_OID, ADEE_TYPE, ADEE_ID) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_FILAXS _value : _values) {
					st.setBigDecimal(  1, _value.file_oid);
					st.setString    (  2, _value.adee_type);
					st.setString    (  3, _value.adee_id);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_FILTERING_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString item = new FieldString("ITEM");
		public final FieldString value = new FieldString("VALUE");
		
		public WFA_FILTERING_Dao(Connection cnx) {
			super(cnx, "WFA_FILTERING");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_FILTERING> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_FILTERING");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     ITEM                 = rs.getString    ("ITEM");
					final String     VALUE                = rs.getString    ("VALUE");
					final WFA_FILTERING obj = new WFA_FILTERING(UTL_CODUTL, ITEM, VALUE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_FILTERING> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_FILTERING> res = new ArrayList<WFA_FILTERING>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_FILTERING set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_FILTERING", condition);
			return res;
		}

		public int merge(WFA_FILTERING _value) {
			final String sql =
				  " merge into WFA_FILTERING using dual on (UTL_CODUTL = ? and ITEM = ? and VALUE = ?)"
				+ " when not matched then insert (UTL_CODUTL, ITEM, VALUE) values (?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.item);
				st.setString    (  3, _value.value);
				st.setString    (  4, _value.utl_codutl);
				st.setString    (  5, _value.item);
				st.setString    (  6, _value.value);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_FILTERING _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FILTERING(UTL_CODUTL, ITEM, VALUE) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.item);
				st.setString    (  3, _value.value);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _utl_codutl
				, SqlExpr<String> _item
				, SqlExpr<String> _value
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_FILTERING(UTL_CODUTL, ITEM, VALUE) values(");
			sql.append(_utl_codutl.toSql());
			sql.append(", ").append(_item.toSql());
			sql.append(", ").append(_value.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_utl_codutl.bind(st, parameterIndex);
				_item.bind(st, parameterIndex);
				_value.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_FILTERING> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_FILTERING(UTL_CODUTL, ITEM, VALUE) values(?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_FILTERING _value : _values) {
					st.setString    (  1, _value.utl_codutl);
					st.setString    (  2, _value.item);
					st.setString    (  3, _value.value);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_INVANA_Dao extends Dao {
		private Connection cnx;
		public final FieldString invoice_number = new FieldString("INVOICE_NUMBER");
		public final FieldString service_description = new FieldString("SERVICE_DESCRIPTION");
		public final FieldString charge_type = new FieldString("CHARGE_TYPE");
		public final FieldString charge_description = new FieldString("CHARGE_DESCRIPTION");
		public final FieldBigDecimal charge_amount = new FieldBigDecimal("CHARGE_AMOUNT");
		public final FieldBigDecimal tax_amount = new FieldBigDecimal("TAX_AMOUNT");
		public final FieldString invoicing_currency = new FieldString("INVOICING_CURRENCY");
		public final FieldString site = new FieldString("SITE");
		
		public WFA_INVANA_Dao(Connection cnx) {
			super(cnx, "WFA_INVANA");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_INVANA> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_INVANA");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     INVOICE_NUMBER       = rs.getString    ("INVOICE_NUMBER");
					final String     SERVICE_DESCRIPTION  = rs.getString    ("SERVICE_DESCRIPTION");
					final String     CHARGE_TYPE          = rs.getString    ("CHARGE_TYPE");
					final String     CHARGE_DESCRIPTION   = rs.getString    ("CHARGE_DESCRIPTION");
					final java.math.BigDecimal CHARGE_AMOUNT        = rs.getBigDecimal("CHARGE_AMOUNT");
					final java.math.BigDecimal TAX_AMOUNT           = rs.getBigDecimal("TAX_AMOUNT");
					final String     INVOICING_CURRENCY   = rs.getString    ("INVOICING_CURRENCY");
					final String     SITE                 = rs.getString    ("SITE");
					final WFA_INVANA obj = new WFA_INVANA(INVOICE_NUMBER, SERVICE_DESCRIPTION, CHARGE_TYPE, CHARGE_DESCRIPTION, CHARGE_AMOUNT, TAX_AMOUNT, INVOICING_CURRENCY, SITE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_INVANA> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_INVANA> res = new ArrayList<WFA_INVANA>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_INVANA set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_INVANA", condition);
			return res;
		}

		public int merge(WFA_INVANA _value) {
			final String sql =
				  " merge into WFA_INVANA using dual on (INVOICE_NUMBER = ? and SERVICE_DESCRIPTION = ? and CHARGE_TYPE = ? and CHARGE_DESCRIPTION = ? and SITE = ?)"
				+ " when matched then update set CHARGE_AMOUNT = ?, TAX_AMOUNT = ?, INVOICING_CURRENCY = ?"
				+ " when not matched then insert (INVOICE_NUMBER, SERVICE_DESCRIPTION, CHARGE_TYPE, CHARGE_DESCRIPTION, CHARGE_AMOUNT, TAX_AMOUNT, INVOICING_CURRENCY, SITE) values (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.invoice_number);
				st.setString    (  2, _value.service_description);
				st.setString    (  3, _value.charge_type);
				st.setString    (  4, _value.charge_description);
				st.setString    (  5, _value.site);
				st.setBigDecimal(  6, _value.charge_amount);
				st.setBigDecimal(  7, _value.tax_amount);
				st.setString    (  8, _value.invoicing_currency);
				st.setString    (  9, _value.invoice_number);
				st.setString    ( 10, _value.service_description);
				st.setString    ( 11, _value.charge_type);
				st.setString    ( 12, _value.charge_description);
				st.setBigDecimal( 13, _value.charge_amount);
				st.setBigDecimal( 14, _value.tax_amount);
				st.setString    ( 15, _value.invoicing_currency);
				st.setString    ( 16, _value.site);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_INVANA _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_INVANA(INVOICE_NUMBER, SERVICE_DESCRIPTION, CHARGE_TYPE, CHARGE_DESCRIPTION, CHARGE_AMOUNT, TAX_AMOUNT, INVOICING_CURRENCY, SITE) values(?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.invoice_number);
				st.setString    (  2, _value.service_description);
				st.setString    (  3, _value.charge_type);
				st.setString    (  4, _value.charge_description);
				st.setBigDecimal(  5, _value.charge_amount);
				st.setBigDecimal(  6, _value.tax_amount);
				st.setString    (  7, _value.invoicing_currency);
				st.setString    (  8, _value.site);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _invoice_number
				, SqlExpr<String> _service_description
				, SqlExpr<String> _charge_type
				, SqlExpr<String> _charge_description
				, SqlExpr<java.math.BigDecimal> _charge_amount
				, SqlExpr<java.math.BigDecimal> _tax_amount
				, SqlExpr<String> _invoicing_currency
				, SqlExpr<String> _site
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_INVANA(INVOICE_NUMBER, SERVICE_DESCRIPTION, CHARGE_TYPE, CHARGE_DESCRIPTION, CHARGE_AMOUNT, TAX_AMOUNT, INVOICING_CURRENCY, SITE) values(");
			sql.append(_invoice_number.toSql());
			sql.append(", ").append(_service_description.toSql());
			sql.append(", ").append(_charge_type.toSql());
			sql.append(", ").append(_charge_description.toSql());
			sql.append(", ").append(_charge_amount.toSql());
			sql.append(", ").append(_tax_amount.toSql());
			sql.append(", ").append(_invoicing_currency.toSql());
			sql.append(", ").append(_site.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_invoice_number.bind(st, parameterIndex);
				_service_description.bind(st, parameterIndex);
				_charge_type.bind(st, parameterIndex);
				_charge_description.bind(st, parameterIndex);
				_charge_amount.bind(st, parameterIndex);
				_tax_amount.bind(st, parameterIndex);
				_invoicing_currency.bind(st, parameterIndex);
				_site.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_INVANA> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_INVANA(INVOICE_NUMBER, SERVICE_DESCRIPTION, CHARGE_TYPE, CHARGE_DESCRIPTION, CHARGE_AMOUNT, TAX_AMOUNT, INVOICING_CURRENCY, SITE) values(?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_INVANA _value : _values) {
					st.setString    (  1, _value.invoice_number);
					st.setString    (  2, _value.service_description);
					st.setString    (  3, _value.charge_type);
					st.setString    (  4, _value.charge_description);
					st.setBigDecimal(  5, _value.charge_amount);
					st.setBigDecimal(  6, _value.tax_amount);
					st.setString    (  7, _value.invoicing_currency);
					st.setString    (  8, _value.site);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_NOTIF_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal notif_id = new FieldBigDecimal("NOTIF_ID");
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString notif_type = new FieldString("NOTIF_TYPE");
		public final FieldString notif_email = new FieldString("NOTIF_EMAIL");
		public final FieldBigDecimal notif_activation = new FieldBigDecimal("NOTIF_ACTIVATION");
		public final FieldString format = new FieldString("FORMAT");
		public final FieldString language = new FieldString("LANGUAGE");
		
		public WFA_NOTIF_Dao(Connection cnx) {
			super(cnx, "WFA_NOTIF");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_NOTIF> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_NOTIF");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal NOTIF_ID             = rs.getBigDecimal("NOTIF_ID");
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     NOTIF_TYPE           = rs.getString    ("NOTIF_TYPE");
					final String     NOTIF_EMAIL          = rs.getString    ("NOTIF_EMAIL");
					final java.math.BigDecimal NOTIF_ACTIVATION     = rs.getBigDecimal("NOTIF_ACTIVATION");
					final String     FORMAT               = rs.getString    ("FORMAT");
					final String     LANGUAGE             = rs.getString    ("LANGUAGE");
					final WFA_NOTIF obj = new WFA_NOTIF(NOTIF_ID, UTL_CODUTL, NOTIF_TYPE, NOTIF_EMAIL, NOTIF_ACTIVATION, FORMAT, LANGUAGE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_NOTIF> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_NOTIF> res = new ArrayList<WFA_NOTIF>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_NOTIF set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_NOTIF", condition);
			return res;
		}

		public int merge(WFA_NOTIF _value) {
			final String sql =
				  " merge into WFA_NOTIF using dual on (NOTIF_ID = ?)"
				+ " when matched then update set UTL_CODUTL = ?, NOTIF_TYPE = ?, NOTIF_EMAIL = ?, NOTIF_ACTIVATION = ?, FORMAT = ?, LANGUAGE = ?"
				+ " when not matched then insert (NOTIF_ID, UTL_CODUTL, NOTIF_TYPE, NOTIF_EMAIL, NOTIF_ACTIVATION, FORMAT, LANGUAGE) values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.notif_id);
				st.setString    (  2, _value.utl_codutl);
				st.setString    (  3, _value.notif_type);
				st.setString    (  4, _value.notif_email);
				st.setBigDecimal(  5, _value.notif_activation);
				st.setString    (  6, _value.format);
				st.setString    (  7, _value.language);
				st.setBigDecimal(  8, _value.notif_id);
				st.setString    (  9, _value.utl_codutl);
				st.setString    ( 10, _value.notif_type);
				st.setString    ( 11, _value.notif_email);
				st.setBigDecimal( 12, _value.notif_activation);
				st.setString    ( 13, _value.format);
				st.setString    ( 14, _value.language);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_NOTIF _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_NOTIF(NOTIF_ID, UTL_CODUTL, NOTIF_TYPE, NOTIF_EMAIL, NOTIF_ACTIVATION, FORMAT, LANGUAGE) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.notif_id);
				st.setString    (  2, _value.utl_codutl);
				st.setString    (  3, _value.notif_type);
				st.setString    (  4, _value.notif_email);
				st.setBigDecimal(  5, _value.notif_activation);
				st.setString    (  6, _value.format);
				st.setString    (  7, _value.language);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _notif_id
				, SqlExpr<String> _utl_codutl
				, SqlExpr<String> _notif_type
				, SqlExpr<String> _notif_email
				, SqlExpr<java.math.BigDecimal> _notif_activation
				, SqlExpr<String> _format
				, SqlExpr<String> _language
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_NOTIF(NOTIF_ID, UTL_CODUTL, NOTIF_TYPE, NOTIF_EMAIL, NOTIF_ACTIVATION, FORMAT, LANGUAGE) values(");
			sql.append(_notif_id.toSql());
			sql.append(", ").append(_utl_codutl.toSql());
			sql.append(", ").append(_notif_type.toSql());
			sql.append(", ").append(_notif_email.toSql());
			sql.append(", ").append(_notif_activation.toSql());
			sql.append(", ").append(_format.toSql());
			sql.append(", ").append(_language.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_notif_id.bind(st, parameterIndex);
				_utl_codutl.bind(st, parameterIndex);
				_notif_type.bind(st, parameterIndex);
				_notif_email.bind(st, parameterIndex);
				_notif_activation.bind(st, parameterIndex);
				_format.bind(st, parameterIndex);
				_language.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_NOTIF> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_NOTIF(NOTIF_ID, UTL_CODUTL, NOTIF_TYPE, NOTIF_EMAIL, NOTIF_ACTIVATION, FORMAT, LANGUAGE) values(?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_NOTIF _value : _values) {
					st.setBigDecimal(  1, _value.notif_id);
					st.setString    (  2, _value.utl_codutl);
					st.setString    (  3, _value.notif_type);
					st.setString    (  4, _value.notif_email);
					st.setBigDecimal(  5, _value.notif_activation);
					st.setString    (  6, _value.format);
					st.setString    (  7, _value.language);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_NOTIF_EXCLUDED_BAN_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal notif_id = new FieldBigDecimal("NOTIF_ID");
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		
		public WFA_NOTIF_EXCLUDED_BAN_Dao(Connection cnx) {
			super(cnx, "WFA_NOTIF_EXCLUDED_BAN");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_NOTIF_EXCLUDED_BAN> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_NOTIF_EXCLUDED_BAN");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal NOTIF_ID             = rs.getBigDecimal("NOTIF_ID");
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final WFA_NOTIF_EXCLUDED_BAN obj = new WFA_NOTIF_EXCLUDED_BAN(NOTIF_ID, CPT_NUMCPTFAC);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_NOTIF_EXCLUDED_BAN> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_NOTIF_EXCLUDED_BAN> res = new ArrayList<WFA_NOTIF_EXCLUDED_BAN>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_NOTIF_EXCLUDED_BAN set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_NOTIF_EXCLUDED_BAN", condition);
			return res;
		}

		public int merge(WFA_NOTIF_EXCLUDED_BAN _value) {
			final String sql =
				  " merge into WFA_NOTIF_EXCLUDED_BAN using dual on (NOTIF_ID = ? and CPT_NUMCPTFAC = ?)"
				+ " when not matched then insert (NOTIF_ID, CPT_NUMCPTFAC) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.notif_id);
				st.setString    (  2, _value.cpt_numcptfac);
				st.setBigDecimal(  3, _value.notif_id);
				st.setString    (  4, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_NOTIF_EXCLUDED_BAN _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_NOTIF_EXCLUDED_BAN(NOTIF_ID, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setBigDecimal(  1, _value.notif_id);
				st.setString    (  2, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<java.math.BigDecimal> _notif_id
				, SqlExpr<String> _cpt_numcptfac
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_NOTIF_EXCLUDED_BAN(NOTIF_ID, CPT_NUMCPTFAC) values(");
			sql.append(_notif_id.toSql());
			sql.append(", ").append(_cpt_numcptfac.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_notif_id.bind(st, parameterIndex);
				_cpt_numcptfac.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_NOTIF_EXCLUDED_BAN> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_NOTIF_EXCLUDED_BAN(NOTIF_ID, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_NOTIF_EXCLUDED_BAN _value : _values) {
					st.setBigDecimal(  1, _value.notif_id);
					st.setString    (  2, _value.cpt_numcptfac);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_REPCSV_Dao extends Dao {
		private Connection cnx;
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString csv_nomper = new FieldString("CSV_NOMPER");
		public final FieldString csv_format = new FieldString("CSV_FORMAT");
		public final FieldString csv_nomrep = new FieldString("CSV_NOMREP");
		public final FieldString csv_nomfic = new FieldString("CSV_NOMFIC");
		public final FieldBigDecimal env_id = new FieldBigDecimal("ENV_ID");
		public final FieldBigDecimal is_available = new FieldBigDecimal("IS_AVAILABLE");
		public final FieldBigDecimal is_out_of_scope = new FieldBigDecimal("IS_OUT_OF_SCOPE");
		public final FieldString fac_numfacree = new FieldString("FAC_NUMFACREE");
		
		public WFA_REPCSV_Dao(Connection cnx) {
			super(cnx, "WFA_REPCSV");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_REPCSV> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_REPCSV");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     CSV_NOMPER           = rs.getString    ("CSV_NOMPER");
					final String     CSV_FORMAT           = rs.getString    ("CSV_FORMAT");
					final String     CSV_NOMREP           = rs.getString    ("CSV_NOMREP");
					final String     CSV_NOMFIC           = rs.getString    ("CSV_NOMFIC");
					final java.math.BigDecimal ENV_ID               = rs.getBigDecimal("ENV_ID");
					final java.math.BigDecimal IS_AVAILABLE         = rs.getBigDecimal("IS_AVAILABLE");
					final java.math.BigDecimal IS_OUT_OF_SCOPE      = rs.getBigDecimal("IS_OUT_OF_SCOPE");
					final String     FAC_NUMFACREE        = rs.getString    ("FAC_NUMFACREE");
					final WFA_REPCSV obj = new WFA_REPCSV(CPT_NUMCPTFAC, CSV_NOMPER, CSV_FORMAT, CSV_NOMREP, CSV_NOMFIC, ENV_ID, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_NUMFACREE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_REPCSV> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_REPCSV> res = new ArrayList<WFA_REPCSV>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_REPCSV set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_REPCSV", condition);
			return res;
		}

		public int merge(WFA_REPCSV _value) {
			final String sql =
				  " merge into WFA_REPCSV using dual on (CPT_NUMCPTFAC = ? and CSV_NOMPER = ? and CSV_FORMAT = ? and CSV_NOMREP = ? and CSV_NOMFIC = ? and ENV_ID = ? and IS_AVAILABLE = ? and IS_OUT_OF_SCOPE = ? and FAC_NUMFACREE = ?)"
				+ " when not matched then insert (CPT_NUMCPTFAC, CSV_NOMPER, CSV_FORMAT, CSV_NOMREP, CSV_NOMFIC, ENV_ID, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_NUMFACREE) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.csv_nomper);
				st.setString    (  3, _value.csv_format);
				st.setString    (  4, _value.csv_nomrep);
				st.setString    (  5, _value.csv_nomfic);
				st.setBigDecimal(  6, _value.env_id);
				st.setBigDecimal(  7, _value.is_available);
				st.setBigDecimal(  8, _value.is_out_of_scope);
				st.setString    (  9, _value.fac_numfacree);
				st.setString    ( 10, _value.cpt_numcptfac);
				st.setString    ( 11, _value.csv_nomper);
				st.setString    ( 12, _value.csv_format);
				st.setString    ( 13, _value.csv_nomrep);
				st.setString    ( 14, _value.csv_nomfic);
				st.setBigDecimal( 15, _value.env_id);
				st.setBigDecimal( 16, _value.is_available);
				st.setBigDecimal( 17, _value.is_out_of_scope);
				st.setString    ( 18, _value.fac_numfacree);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_REPCSV _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_REPCSV(CPT_NUMCPTFAC, CSV_NOMPER, CSV_FORMAT, CSV_NOMREP, CSV_NOMFIC, ENV_ID, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_NUMFACREE) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.cpt_numcptfac);
				st.setString    (  2, _value.csv_nomper);
				st.setString    (  3, _value.csv_format);
				st.setString    (  4, _value.csv_nomrep);
				st.setString    (  5, _value.csv_nomfic);
				st.setBigDecimal(  6, _value.env_id);
				st.setBigDecimal(  7, _value.is_available);
				st.setBigDecimal(  8, _value.is_out_of_scope);
				st.setString    (  9, _value.fac_numfacree);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _cpt_numcptfac
				, SqlExpr<String> _csv_nomper
				, SqlExpr<String> _csv_format
				, SqlExpr<String> _csv_nomrep
				, SqlExpr<String> _csv_nomfic
				, SqlExpr<java.math.BigDecimal> _env_id
				, SqlExpr<java.math.BigDecimal> _is_available
				, SqlExpr<java.math.BigDecimal> _is_out_of_scope
				, SqlExpr<String> _fac_numfacree
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_REPCSV(CPT_NUMCPTFAC, CSV_NOMPER, CSV_FORMAT, CSV_NOMREP, CSV_NOMFIC, ENV_ID, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_NUMFACREE) values(");
			sql.append(_cpt_numcptfac.toSql());
			sql.append(", ").append(_csv_nomper.toSql());
			sql.append(", ").append(_csv_format.toSql());
			sql.append(", ").append(_csv_nomrep.toSql());
			sql.append(", ").append(_csv_nomfic.toSql());
			sql.append(", ").append(_env_id.toSql());
			sql.append(", ").append(_is_available.toSql());
			sql.append(", ").append(_is_out_of_scope.toSql());
			sql.append(", ").append(_fac_numfacree.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_cpt_numcptfac.bind(st, parameterIndex);
				_csv_nomper.bind(st, parameterIndex);
				_csv_format.bind(st, parameterIndex);
				_csv_nomrep.bind(st, parameterIndex);
				_csv_nomfic.bind(st, parameterIndex);
				_env_id.bind(st, parameterIndex);
				_is_available.bind(st, parameterIndex);
				_is_out_of_scope.bind(st, parameterIndex);
				_fac_numfacree.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_REPCSV> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_REPCSV(CPT_NUMCPTFAC, CSV_NOMPER, CSV_FORMAT, CSV_NOMREP, CSV_NOMFIC, ENV_ID, IS_AVAILABLE, IS_OUT_OF_SCOPE, FAC_NUMFACREE) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_REPCSV _value : _values) {
					st.setString    (  1, _value.cpt_numcptfac);
					st.setString    (  2, _value.csv_nomper);
					st.setString    (  3, _value.csv_format);
					st.setString    (  4, _value.csv_nomrep);
					st.setString    (  5, _value.csv_nomfic);
					st.setBigDecimal(  6, _value.env_id);
					st.setBigDecimal(  7, _value.is_available);
					st.setBigDecimal(  8, _value.is_out_of_scope);
					st.setString    (  9, _value.fac_numfacree);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_RSC_Dao extends Dao {
		private Connection cnx;
		public final FieldString rsc_code = new FieldString("RSC_CODE");
		public final FieldString rsc_country = new FieldString("RSC_COUNTRY");
		public final FieldString rsc_name = new FieldString("RSC_NAME");
		public final FieldBigDecimal local_printing = new FieldBigDecimal("LOCAL_PRINTING");
		public final FieldString country_name = new FieldString("COUNTRY_NAME");
		
		public WFA_RSC_Dao(Connection cnx) {
			super(cnx, "WFA_RSC");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_RSC> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_RSC");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     RSC_CODE             = rs.getString    ("RSC_CODE");
					final String     RSC_COUNTRY          = rs.getString    ("RSC_COUNTRY");
					final String     RSC_NAME             = rs.getString    ("RSC_NAME");
					final java.math.BigDecimal LOCAL_PRINTING       = rs.getBigDecimal("LOCAL_PRINTING");
					final String     COUNTRY_NAME         = rs.getString    ("COUNTRY_NAME");
					final WFA_RSC obj = new WFA_RSC(RSC_CODE, RSC_COUNTRY, RSC_NAME, LOCAL_PRINTING, COUNTRY_NAME);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_RSC> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_RSC> res = new ArrayList<WFA_RSC>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_RSC set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_RSC", condition);
			return res;
		}

		public int merge(WFA_RSC _value) {
			final String sql =
				  " merge into WFA_RSC using dual on (RSC_CODE = ?)"
				+ " when matched then update set RSC_COUNTRY = ?, RSC_NAME = ?, LOCAL_PRINTING = ?, COUNTRY_NAME = ?"
				+ " when not matched then insert (RSC_CODE, RSC_COUNTRY, RSC_NAME, LOCAL_PRINTING, COUNTRY_NAME) values (?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.rsc_code);
				st.setString    (  2, _value.rsc_country);
				st.setString    (  3, _value.rsc_name);
				st.setBigDecimal(  4, _value.local_printing);
				st.setString    (  5, _value.country_name);
				st.setString    (  6, _value.rsc_code);
				st.setString    (  7, _value.rsc_country);
				st.setString    (  8, _value.rsc_name);
				st.setBigDecimal(  9, _value.local_printing);
				st.setString    ( 10, _value.country_name);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_RSC _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_RSC(RSC_CODE, RSC_COUNTRY, RSC_NAME, LOCAL_PRINTING, COUNTRY_NAME) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.rsc_code);
				st.setString    (  2, _value.rsc_country);
				st.setString    (  3, _value.rsc_name);
				st.setBigDecimal(  4, _value.local_printing);
				st.setString    (  5, _value.country_name);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _rsc_code
				, SqlExpr<String> _rsc_country
				, SqlExpr<String> _rsc_name
				, SqlExpr<java.math.BigDecimal> _local_printing
				, SqlExpr<String> _country_name
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_RSC(RSC_CODE, RSC_COUNTRY, RSC_NAME, LOCAL_PRINTING, COUNTRY_NAME) values(");
			sql.append(_rsc_code.toSql());
			sql.append(", ").append(_rsc_country.toSql());
			sql.append(", ").append(_rsc_name.toSql());
			sql.append(", ").append(_local_printing.toSql());
			sql.append(", ").append(_country_name.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_rsc_code.bind(st, parameterIndex);
				_rsc_country.bind(st, parameterIndex);
				_rsc_name.bind(st, parameterIndex);
				_local_printing.bind(st, parameterIndex);
				_country_name.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_RSC> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_RSC(RSC_CODE, RSC_COUNTRY, RSC_NAME, LOCAL_PRINTING, COUNTRY_NAME) values(?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_RSC _value : _values) {
					st.setString    (  1, _value.rsc_code);
					st.setString    (  2, _value.rsc_country);
					st.setString    (  3, _value.rsc_name);
					st.setBigDecimal(  4, _value.local_printing);
					st.setString    (  5, _value.country_name);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_TRACES_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldTimestamp trc_datfinses = new FieldTimestamp("TRC_DATFINSES");
		public final FieldBigDecimal trc_nbredt = new FieldBigDecimal("TRC_NBREDT");
		public final FieldString trc_logged_as_user = new FieldString("TRC_LOGGED_AS_USER");
		public final FieldString trc_module_code = new FieldString("TRC_MODULE_CODE");
		public final FieldString trc_action_code = new FieldString("TRC_ACTION_CODE");
		public final FieldString trc_action_sub_code = new FieldString("TRC_ACTION_SUB_CODE");
		public final FieldString trc_object_class = new FieldString("TRC_OBJECT_CLASS");
		public final FieldString trc_object_id = new FieldString("TRC_OBJECT_ID");
		
		public WFA_TRACES_Dao(Connection cnx) {
			super(cnx, "WFA_TRACES");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_TRACES> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_TRACES");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final java.sql.Timestamp TRC_DATFINSES        = rs.getTimestamp ("TRC_DATFINSES");
					final java.math.BigDecimal TRC_NBREDT           = rs.getBigDecimal("TRC_NBREDT");
					final String     TRC_LOGGED_AS_USER   = rs.getString    ("TRC_LOGGED_AS_USER");
					final String     TRC_MODULE_CODE      = rs.getString    ("TRC_MODULE_CODE");
					final String     TRC_ACTION_CODE      = rs.getString    ("TRC_ACTION_CODE");
					final String     TRC_ACTION_SUB_CODE  = rs.getString    ("TRC_ACTION_SUB_CODE");
					final String     TRC_OBJECT_CLASS     = rs.getString    ("TRC_OBJECT_CLASS");
					final String     TRC_OBJECT_ID        = rs.getString    ("TRC_OBJECT_ID");
					final WFA_TRACES obj = new WFA_TRACES(UTL_CODUTL, TRC_DATFINSES, TRC_NBREDT, TRC_LOGGED_AS_USER, TRC_MODULE_CODE, TRC_ACTION_CODE, TRC_ACTION_SUB_CODE, TRC_OBJECT_CLASS, TRC_OBJECT_ID);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_TRACES> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_TRACES> res = new ArrayList<WFA_TRACES>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_TRACES set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_TRACES", condition);
			return res;
		}

		public int merge(WFA_TRACES _value) {
			final String sql =
				  " merge into WFA_TRACES using dual on (UTL_CODUTL = ? and TRC_DATFINSES = ? and TRC_NBREDT = ? and TRC_LOGGED_AS_USER = ? and TRC_MODULE_CODE = ? and TRC_ACTION_CODE = ? and TRC_ACTION_SUB_CODE = ? and TRC_OBJECT_CLASS = ? and TRC_OBJECT_ID = ?)"
				+ " when not matched then insert (UTL_CODUTL, TRC_DATFINSES, TRC_NBREDT, TRC_LOGGED_AS_USER, TRC_MODULE_CODE, TRC_ACTION_CODE, TRC_ACTION_SUB_CODE, TRC_OBJECT_CLASS, TRC_OBJECT_ID) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setTimestamp (  2, _value.trc_datfinses);
				st.setBigDecimal(  3, _value.trc_nbredt);
				st.setString    (  4, _value.trc_logged_as_user);
				st.setString    (  5, _value.trc_module_code);
				st.setString    (  6, _value.trc_action_code);
				st.setString    (  7, _value.trc_action_sub_code);
				st.setString    (  8, _value.trc_object_class);
				st.setString    (  9, _value.trc_object_id);
				st.setString    ( 10, _value.utl_codutl);
				st.setTimestamp ( 11, _value.trc_datfinses);
				st.setBigDecimal( 12, _value.trc_nbredt);
				st.setString    ( 13, _value.trc_logged_as_user);
				st.setString    ( 14, _value.trc_module_code);
				st.setString    ( 15, _value.trc_action_code);
				st.setString    ( 16, _value.trc_action_sub_code);
				st.setString    ( 17, _value.trc_object_class);
				st.setString    ( 18, _value.trc_object_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_TRACES _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_TRACES(UTL_CODUTL, TRC_DATFINSES, TRC_NBREDT, TRC_LOGGED_AS_USER, TRC_MODULE_CODE, TRC_ACTION_CODE, TRC_ACTION_SUB_CODE, TRC_OBJECT_CLASS, TRC_OBJECT_ID) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setTimestamp (  2, _value.trc_datfinses);
				st.setBigDecimal(  3, _value.trc_nbredt);
				st.setString    (  4, _value.trc_logged_as_user);
				st.setString    (  5, _value.trc_module_code);
				st.setString    (  6, _value.trc_action_code);
				st.setString    (  7, _value.trc_action_sub_code);
				st.setString    (  8, _value.trc_object_class);
				st.setString    (  9, _value.trc_object_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _utl_codutl
				, SqlExpr<java.sql.Timestamp> _trc_datfinses
				, SqlExpr<java.math.BigDecimal> _trc_nbredt
				, SqlExpr<String> _trc_logged_as_user
				, SqlExpr<String> _trc_module_code
				, SqlExpr<String> _trc_action_code
				, SqlExpr<String> _trc_action_sub_code
				, SqlExpr<String> _trc_object_class
				, SqlExpr<String> _trc_object_id
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_TRACES(UTL_CODUTL, TRC_DATFINSES, TRC_NBREDT, TRC_LOGGED_AS_USER, TRC_MODULE_CODE, TRC_ACTION_CODE, TRC_ACTION_SUB_CODE, TRC_OBJECT_CLASS, TRC_OBJECT_ID) values(");
			sql.append(_utl_codutl.toSql());
			sql.append(", ").append(_trc_datfinses.toSql());
			sql.append(", ").append(_trc_nbredt.toSql());
			sql.append(", ").append(_trc_logged_as_user.toSql());
			sql.append(", ").append(_trc_module_code.toSql());
			sql.append(", ").append(_trc_action_code.toSql());
			sql.append(", ").append(_trc_action_sub_code.toSql());
			sql.append(", ").append(_trc_object_class.toSql());
			sql.append(", ").append(_trc_object_id.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_utl_codutl.bind(st, parameterIndex);
				_trc_datfinses.bind(st, parameterIndex);
				_trc_nbredt.bind(st, parameterIndex);
				_trc_logged_as_user.bind(st, parameterIndex);
				_trc_module_code.bind(st, parameterIndex);
				_trc_action_code.bind(st, parameterIndex);
				_trc_action_sub_code.bind(st, parameterIndex);
				_trc_object_class.bind(st, parameterIndex);
				_trc_object_id.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_TRACES> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_TRACES(UTL_CODUTL, TRC_DATFINSES, TRC_NBREDT, TRC_LOGGED_AS_USER, TRC_MODULE_CODE, TRC_ACTION_CODE, TRC_ACTION_SUB_CODE, TRC_OBJECT_CLASS, TRC_OBJECT_ID) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_TRACES _value : _values) {
					st.setString    (  1, _value.utl_codutl);
					st.setTimestamp (  2, _value.trc_datfinses);
					st.setBigDecimal(  3, _value.trc_nbredt);
					st.setString    (  4, _value.trc_logged_as_user);
					st.setString    (  5, _value.trc_module_code);
					st.setString    (  6, _value.trc_action_code);
					st.setString    (  7, _value.trc_action_sub_code);
					st.setString    (  8, _value.trc_object_class);
					st.setString    (  9, _value.trc_object_id);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_UTL_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString utl_isadm = new FieldString("UTL_ISADM");
		public final FieldBigDecimal utl_ctc_id = new FieldBigDecimal("UTL_CTC_ID");
		public final FieldString utl_siu_id = new FieldString("UTL_SIU_ID");
		
		public WFA_UTL_Dao(Connection cnx) {
			super(cnx, "WFA_UTL");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_UTL> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_UTL");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     UTL_ISADM            = rs.getString    ("UTL_ISADM");
					final java.math.BigDecimal UTL_CTC_ID           = rs.getBigDecimal("UTL_CTC_ID");
					final String     UTL_SIU_ID           = rs.getString    ("UTL_SIU_ID");
					final WFA_UTL obj = new WFA_UTL(UTL_CODUTL, UTL_ISADM, UTL_CTC_ID, UTL_SIU_ID);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_UTL> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_UTL> res = new ArrayList<WFA_UTL>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_UTL set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_UTL", condition);
			return res;
		}

		public int merge(WFA_UTL _value) {
			final String sql =
				  " merge into WFA_UTL using dual on (UTL_CODUTL = ?)"
				+ " when matched then update set UTL_ISADM = ?, UTL_CTC_ID = ?, UTL_SIU_ID = ?"
				+ " when not matched then insert (UTL_CODUTL, UTL_ISADM, UTL_CTC_ID, UTL_SIU_ID) values (?, ?, ?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.utl_isadm);
				st.setBigDecimal(  3, _value.utl_ctc_id);
				st.setString    (  4, _value.utl_siu_id);
				st.setString    (  5, _value.utl_codutl);
				st.setString    (  6, _value.utl_isadm);
				st.setBigDecimal(  7, _value.utl_ctc_id);
				st.setString    (  8, _value.utl_siu_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_UTL _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTL(UTL_CODUTL, UTL_ISADM, UTL_CTC_ID, UTL_SIU_ID) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.utl_isadm);
				st.setBigDecimal(  3, _value.utl_ctc_id);
				st.setString    (  4, _value.utl_siu_id);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _utl_codutl
				, SqlExpr<String> _utl_isadm
				, SqlExpr<java.math.BigDecimal> _utl_ctc_id
				, SqlExpr<String> _utl_siu_id
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_UTL(UTL_CODUTL, UTL_ISADM, UTL_CTC_ID, UTL_SIU_ID) values(");
			sql.append(_utl_codutl.toSql());
			sql.append(", ").append(_utl_isadm.toSql());
			sql.append(", ").append(_utl_ctc_id.toSql());
			sql.append(", ").append(_utl_siu_id.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_utl_codutl.bind(st, parameterIndex);
				_utl_isadm.bind(st, parameterIndex);
				_utl_ctc_id.bind(st, parameterIndex);
				_utl_siu_id.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_UTL> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTL(UTL_CODUTL, UTL_ISADM, UTL_CTC_ID, UTL_SIU_ID) values(?, ?, ?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_UTL _value : _values) {
					st.setString    (  1, _value.utl_codutl);
					st.setString    (  2, _value.utl_isadm);
					st.setBigDecimal(  3, _value.utl_ctc_id);
					st.setString    (  4, _value.utl_siu_id);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_UTLCPT_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		
		public WFA_UTLCPT_Dao(Connection cnx) {
			super(cnx, "WFA_UTLCPT");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_UTLCPT> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_UTLCPT");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final WFA_UTLCPT obj = new WFA_UTLCPT(UTL_CODUTL, CPT_NUMCPTFAC);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_UTLCPT> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_UTLCPT> res = new ArrayList<WFA_UTLCPT>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_UTLCPT set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_UTLCPT", condition);
			return res;
		}

		public int merge(WFA_UTLCPT _value) {
			final String sql =
				  " merge into WFA_UTLCPT using dual on (UTL_CODUTL = ? and CPT_NUMCPTFAC = ?)"
				+ " when not matched then insert (UTL_CODUTL, CPT_NUMCPTFAC) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.cpt_numcptfac);
				st.setString    (  3, _value.utl_codutl);
				st.setString    (  4, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_UTLCPT _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTLCPT(UTL_CODUTL, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.cpt_numcptfac);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _utl_codutl
				, SqlExpr<String> _cpt_numcptfac
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_UTLCPT(UTL_CODUTL, CPT_NUMCPTFAC) values(");
			sql.append(_utl_codutl.toSql());
			sql.append(", ").append(_cpt_numcptfac.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_utl_codutl.bind(st, parameterIndex);
				_cpt_numcptfac.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_UTLCPT> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTLCPT(UTL_CODUTL, CPT_NUMCPTFAC) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_UTLCPT _value : _values) {
					st.setString    (  1, _value.utl_codutl);
					st.setString    (  2, _value.cpt_numcptfac);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_UTLCUS_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		
		public WFA_UTLCUS_Dao(Connection cnx) {
			super(cnx, "WFA_UTLCUS");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_UTLCUS> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_UTLCUS");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final WFA_UTLCUS obj = new WFA_UTLCUS(UTL_CODUTL, CUS_NUMCUS);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_UTLCUS> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_UTLCUS> res = new ArrayList<WFA_UTLCUS>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

		public int update(Collection<UpdateSetClause> updates, Condition condition) {
			assert updates != null;
			assert updates.size() >= 1;
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder();
			sql.append("update WFA_UTLCUS set ");
			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();
			sql.append(DaoUtil.join(updates_str.iterator(), ", "));
			if (condition != null) sql.append(" where ").append(condition.toSql());
			final Sequence parameterIndex = new Sequence(1);
			try {
				st = cnx.prepareStatement(sql.toString());
				for (UpdateSetClause update : updates) {
					update.bind(st, parameterIndex);
				}
				if (condition != null) condition.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int delete(Condition condition) {
			int res = DaoUtil.delete(cnx, "WFA_UTLCUS", condition);
			return res;
		}

		public int merge(WFA_UTLCUS _value) {
			final String sql =
				  " merge into WFA_UTLCUS using dual on (UTL_CODUTL = ? and CUS_NUMCUS = ?)"
				+ " when not matched then insert (UTL_CODUTL, CUS_NUMCUS) values (?, ?)";
			PreparedStatement st = null;
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.cus_numcus);
				st.setString    (  3, _value.utl_codutl);
				st.setString    (  4, _value.cus_numcus);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public int insert(WFA_UTLCUS _value) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTLCUS(UTL_CODUTL, CUS_NUMCUS) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				st.setString    (  1, _value.utl_codutl);
				st.setString    (  2, _value.cus_numcus);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int insert(
				  SqlExpr<String> _utl_codutl
				, SqlExpr<String> _cus_numcus
		) {
			PreparedStatement st = null;
			final StringBuilder sql = new StringBuilder("insert into WFA_UTLCUS(UTL_CODUTL, CUS_NUMCUS) values(");
			sql.append(_utl_codutl.toSql());
			sql.append(", ").append(_cus_numcus.toSql());
			sql.append(")");
			try {
				st = cnx.prepareStatement(sql.toString());
				Sequence parameterIndex = new Sequence(1);
				_utl_codutl.bind(st, parameterIndex);
				_cus_numcus.bind(st, parameterIndex);
				final int nRows = st.executeUpdate();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
		public int[] insertBatch(Iterable<WFA_UTLCUS> _values) {
			PreparedStatement st = null;
			final String sql = "insert into WFA_UTLCUS(UTL_CODUTL, CUS_NUMCUS) values(?, ?)";
			try {
				st = cnx.prepareStatement(sql);
				for (WFA_UTLCUS _value : _values) {
					st.setString    (  1, _value.utl_codutl);
					st.setString    (  2, _value.cus_numcus);
					st.addBatch();
				}
				final int[] nRows = st.executeBatch();
				cnx.commit();
				return nRows;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}
	}

	public static class WFA_VIEW_CFDITT_Dao extends Dao {
		private Connection cnx;
		public final FieldString utl_codutl = new FieldString("UTL_CODUTL");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString link_utl_cus = new FieldString("LINK_UTL_CUS");
		public final FieldString cfd_codaplemi = new FieldString("CFD_CODAPLEMI");
		public final FieldString cfd_idtope = new FieldString("CFD_IDTOPE");
		public final FieldString cfd_libgrp = new FieldString("CFD_LIBGRP");
		public final FieldString cfd_codac = new FieldString("CFD_CODAC");
		public final FieldString cat_idtcat = new FieldString("CAT_IDTCAT");
		
		public WFA_VIEW_CFDITT_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_CFDITT");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_CFDITT> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_CFDITT");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     UTL_CODUTL           = rs.getString    ("UTL_CODUTL");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     LINK_UTL_CUS         = rs.getString    ("LINK_UTL_CUS");
					final String     CFD_CODAPLEMI        = rs.getString    ("CFD_CODAPLEMI");
					final String     CFD_IDTOPE           = rs.getString    ("CFD_IDTOPE");
					final String     CFD_LIBGRP           = rs.getString    ("CFD_LIBGRP");
					final String     CFD_CODAC            = rs.getString    ("CFD_CODAC");
					final String     CAT_IDTCAT           = rs.getString    ("CAT_IDTCAT");
					final WFA_VIEW_CFDITT obj = new WFA_VIEW_CFDITT(UTL_CODUTL, CUS_NUMCUS, CPT_NUMCPTFAC, LINK_UTL_CUS, CFD_CODAPLEMI, CFD_IDTOPE, CFD_LIBGRP, CFD_CODAC, CAT_IDTCAT);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_CFDITT> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_CFDITT> res = new ArrayList<WFA_VIEW_CFDITT>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_EI_ADDRESS_STATUS_Dao extends Dao {
		private Connection cnx;
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString validation_status = new FieldString("VALIDATION_STATUS");
		
		public WFA_VIEW_EI_ADDRESS_STATUS_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_EI_ADDRESS_STATUS");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_EI_ADDRESS_STATUS> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_EI_ADDRESS_STATUS");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     VALIDATION_STATUS    = rs.getString    ("VALIDATION_STATUS");
					final WFA_VIEW_EI_ADDRESS_STATUS obj = new WFA_VIEW_EI_ADDRESS_STATUS(EMAIL, VALIDATION_STATUS);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_EI_ADDRESS_STATUS> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_EI_ADDRESS_STATUS> res = new ArrayList<WFA_VIEW_EI_ADDRESS_STATUS>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_EI_BAN_Dao extends Dao {
		private Connection cnx;
		public final FieldString cpt_numcptfac = new FieldString("CPT_NUMCPTFAC");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString origin = new FieldString("ORIGIN");
		
		public WFA_VIEW_EI_BAN_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_EI_BAN");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_EI_BAN> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_EI_BAN");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     CPT_NUMCPTFAC        = rs.getString    ("CPT_NUMCPTFAC");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     ORIGIN               = rs.getString    ("ORIGIN");
					final WFA_VIEW_EI_BAN obj = new WFA_VIEW_EI_BAN(CPT_NUMCPTFAC, EMAIL, ORIGIN);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_EI_BAN> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_EI_BAN> res = new ArrayList<WFA_VIEW_EI_BAN>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_EI_MONITORING_Dao extends Dao {
		private Connection cnx;
		public final FieldBigDecimal monitoring_id = new FieldBigDecimal("MONITORING_ID");
		public final FieldString period = new FieldString("PERIOD");
		public final FieldString cus_numcus = new FieldString("CUS_NUMCUS");
		public final FieldString token = new FieldString("TOKEN");
		public final FieldString email_address = new FieldString("EMAIL_ADDRESS");
		public final FieldString email_status = new FieldString("EMAIL_STATUS");
		public final FieldTimestamp action_date = new FieldTimestamp("ACTION_DATE");
		public final FieldString author = new FieldString("AUTHOR");
		public final FieldString user_comment = new FieldString("USER_COMMENT");
		public final FieldTimestamp sending_date = new FieldTimestamp("SENDING_DATE");
		
		public WFA_VIEW_EI_MONITORING_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_EI_MONITORING");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_EI_MONITORING> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_EI_MONITORING");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final java.math.BigDecimal MONITORING_ID        = rs.getBigDecimal("MONITORING_ID");
					final String     PERIOD               = rs.getString    ("PERIOD");
					final String     CUS_NUMCUS           = rs.getString    ("CUS_NUMCUS");
					final String     TOKEN                = rs.getString    ("TOKEN");
					final String     EMAIL_ADDRESS        = rs.getString    ("EMAIL_ADDRESS");
					final String     EMAIL_STATUS         = rs.getString    ("EMAIL_STATUS");
					final java.sql.Timestamp ACTION_DATE          = rs.getTimestamp ("ACTION_DATE");
					final String     AUTHOR               = rs.getString    ("AUTHOR");
					final String     USER_COMMENT         = rs.getString    ("USER_COMMENT");
					final java.sql.Timestamp SENDING_DATE         = rs.getTimestamp ("SENDING_DATE");
					final WFA_VIEW_EI_MONITORING obj = new WFA_VIEW_EI_MONITORING(MONITORING_ID, PERIOD, CUS_NUMCUS, TOKEN, EMAIL_ADDRESS, EMAIL_STATUS, ACTION_DATE, AUTHOR, USER_COMMENT, SENDING_DATE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_EI_MONITORING> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_EI_MONITORING> res = new ArrayList<WFA_VIEW_EI_MONITORING>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_LOGIN_BAN_Dao extends Dao {
		private Connection cnx;
		public final FieldString login = new FieldString("LOGIN");
		public final FieldString firstname = new FieldString("FIRSTNAME");
		public final FieldString lastname = new FieldString("LASTNAME");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString ban = new FieldString("BAN");
		
		public WFA_VIEW_LOGIN_BAN_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_LOGIN_BAN");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_LOGIN_BAN> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_LOGIN_BAN");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     LOGIN                = rs.getString    ("LOGIN");
					final String     FIRSTNAME            = rs.getString    ("FIRSTNAME");
					final String     LASTNAME             = rs.getString    ("LASTNAME");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     BAN                  = rs.getString    ("BAN");
					final WFA_VIEW_LOGIN_BAN obj = new WFA_VIEW_LOGIN_BAN(LOGIN, FIRSTNAME, LASTNAME, EMAIL, BAN);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_LOGIN_BAN> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_LOGIN_BAN> res = new ArrayList<WFA_VIEW_LOGIN_BAN>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_LOGIN_CUSCODE_Dao extends Dao {
		private Connection cnx;
		public final FieldString login = new FieldString("LOGIN");
		public final FieldString firstname = new FieldString("FIRSTNAME");
		public final FieldString lastname = new FieldString("LASTNAME");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString cuscode = new FieldString("CUSCODE");
		
		public WFA_VIEW_LOGIN_CUSCODE_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_LOGIN_CUSCODE");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_LOGIN_CUSCODE> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_LOGIN_CUSCODE");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     LOGIN                = rs.getString    ("LOGIN");
					final String     FIRSTNAME            = rs.getString    ("FIRSTNAME");
					final String     LASTNAME             = rs.getString    ("LASTNAME");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     CUSCODE              = rs.getString    ("CUSCODE");
					final WFA_VIEW_LOGIN_CUSCODE obj = new WFA_VIEW_LOGIN_CUSCODE(LOGIN, FIRSTNAME, LASTNAME, EMAIL, CUSCODE);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_LOGIN_CUSCODE> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_LOGIN_CUSCODE> res = new ArrayList<WFA_VIEW_LOGIN_CUSCODE>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

	public static class WFA_VIEW_LOGIN_CUSCODE_BAN_Dao extends Dao {
		private Connection cnx;
		public final FieldString login = new FieldString("LOGIN");
		public final FieldString firstname = new FieldString("FIRSTNAME");
		public final FieldString lastname = new FieldString("LASTNAME");
		public final FieldString email = new FieldString("EMAIL");
		public final FieldString cuscode = new FieldString("CUSCODE");
		public final FieldString ban = new FieldString("BAN");
		
		public WFA_VIEW_LOGIN_CUSCODE_BAN_Dao(Connection cnx) {
			super(cnx, "WFA_VIEW_LOGIN_CUSCODE_BAN");
			this.cnx = cnx;
		}

		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<WFA_VIEW_LOGIN_CUSCODE_BAN> callback) {
			PreparedStatement st = null;
			final StringBuilder query = new StringBuilder();
			query.append("select * from WFA_VIEW_LOGIN_CUSCODE_BAN");
			if (condition != null) query.append(" where ").append(condition.toSql());
			if (orderBy != null) {
				query.append(" order by ");
				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();
				query.append(DaoUtil.join(orderBy_str.iterator(), ", "));
			}
			try {
				st = cnx.prepareStatement(query.toString());
				if (condition != null) condition.bind(st, new Sequence(1));
				final ResultSet rs = st.executeQuery();
				while(rs.next()) {
					final String     LOGIN                = rs.getString    ("LOGIN");
					final String     FIRSTNAME            = rs.getString    ("FIRSTNAME");
					final String     LASTNAME             = rs.getString    ("LASTNAME");
					final String     EMAIL                = rs.getString    ("EMAIL");
					final String     CUSCODE              = rs.getString    ("CUSCODE");
					final String     BAN                  = rs.getString    ("BAN");
					final WFA_VIEW_LOGIN_CUSCODE_BAN obj = new WFA_VIEW_LOGIN_CUSCODE_BAN(LOGIN, FIRSTNAME, LASTNAME, EMAIL, CUSCODE, BAN);
					callback.accept(obj);
				}
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				DaoUtil.close(st);
			}
		}

		public List<WFA_VIEW_LOGIN_CUSCODE_BAN> search(Condition condition, Collection<OrderByClause> orderBy) {
			List<WFA_VIEW_LOGIN_CUSCODE_BAN> res = new ArrayList<WFA_VIEW_LOGIN_CUSCODE_BAN>();
			search(condition, orderBy, DaoUtil.toList(res));
			return res;
		}

	}

}

