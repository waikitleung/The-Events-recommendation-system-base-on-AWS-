package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")    // mapping to the endpoint
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONArray array = new JSONArray();
		try {
			String userId = request.getParameter("user_id");
			double lat = Double.parseDouble(request.getParameter("lat")); // ÀàÐÍ×ª»»
			double lon = Double.parseDouble(request.getParameter("lon"));
			String keyword = request.getParameter("term");
			
			//we update doGet using DBconnection in which not only get the events but also store them in DB
			DBConnection connection = DBConnectionFactory.getConnection();
			List<Item> items = connection.searchItems(lat, lon, keyword);
			Set<String> favorite = connection.getFavoriteItemIds(userId);
	 		connection.close();

			
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				array.put(obj);
			}
		} catch (Exception e) {
				e.printStackTrace();
			}

			RpcHelper.writeJsonArray(response, array);
	}




	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
